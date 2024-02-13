package com.flipkartapp.es.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipkartapp.es.Exception.InvalidOtpException;
import com.flipkartapp.es.Exception.InvalidUserRoleException;
import com.flipkartapp.es.Exception.OtpExpiredException;
import com.flipkartapp.es.Exception.SessionExpiredException;
import com.flipkartapp.es.Exception.UserAlreadyLoggedInException;
import com.flipkartapp.es.Exception.UserAlreadyRegisteredException;
import com.flipkartapp.es.Exception.UserNotFoundException;
import com.flipkartapp.es.Exception.UserNotLoggedInException;
import com.flipkartapp.es.cache.CacheStore;
import com.flipkartapp.es.entity.AccessToken;
import com.flipkartapp.es.entity.Customer;
import com.flipkartapp.es.entity.RefreshToken;
import com.flipkartapp.es.entity.Seller;
import com.flipkartapp.es.entity.User;
import com.flipkartapp.es.repository.AccessTokenRepository;
import com.flipkartapp.es.repository.CustomerRepository;
import com.flipkartapp.es.repository.RefreshTokenRepository;
import com.flipkartapp.es.repository.SellerRepository;
import com.flipkartapp.es.repository.UserRepository;
import com.flipkartapp.es.requestdto.AuthRequest;
import com.flipkartapp.es.requestdto.OTPModel;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.AuthResponse;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.security.JwtService;
import com.flipkartapp.es.service.AuthService;
import com.flipkartapp.es.util.CookieManager;
import com.flipkartapp.es.util.MessageStructure;
import com.flipkartapp.es.util.ResponseStructure;
import com.flipkartapp.es.util.SimpleResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImplementation implements AuthService {

	UserRepository userRepo;

	SellerRepository sellerRepo;

	CustomerRepository customerRepo;

	private PasswordEncoder passwordEncoder;

	ResponseStructure<UserResponse> structure;

	ResponseStructure<HttpServletResponse> rs;

	private ResponseStructure<AuthResponse> authStructure;

	CacheStore<String> otpCacheStore;

	CacheStore<User> userCacheStore;

	private JavaMailSender javaMailSender;

	private AuthenticationManager authenticationManager;

	private CookieManager cookieManager;

	private JwtService jwtService;

	private AccessTokenRepository accessTRepo;

	private RefreshTokenRepository refreshTRepo;

	private SimpleResponseStructure srs;

	@Value("${myapp.access.expiry}")
	private int accessExpiryInSeconds;

	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInSeconds;

	public AuthServiceImplementation(UserRepository userRepo, SellerRepository sellerRepo,
			CustomerRepository customerRepo, PasswordEncoder passwordEncoder, ResponseStructure<UserResponse> structure,
			ResponseStructure<HttpServletResponse> rs, ResponseStructure<AuthResponse> authStructure,
			CacheStore<String> otpCacheStore, CacheStore<User> userCacheStore, JavaMailSender javaMailSender,
			AuthenticationManager authenticationManager, CookieManager cookieManager, JwtService jwtService,
			SimpleResponseStructure srs, AccessTokenRepository accessTRepo, RefreshTokenRepository refreshTRepo) {
		super();
		this.userRepo = userRepo;
		this.sellerRepo = sellerRepo;
		this.customerRepo = customerRepo;
		this.passwordEncoder = passwordEncoder;
		this.structure = structure;
		this.otpCacheStore = otpCacheStore;
		this.userCacheStore = userCacheStore;
		this.javaMailSender = javaMailSender;
		this.authenticationManager = authenticationManager;
		this.cookieManager = cookieManager;
		this.jwtService = jwtService;
		this.accessTRepo = accessTRepo;
		this.refreshTRepo = refreshTRepo;
		this.authStructure = authStructure;
		this.rs = rs;
		this.srs = srs;
	}

	@SuppressWarnings("unchecked")
	<T extends User> T mapToUser(UserRequest userRequest) {
		User user = null;

		switch (userRequest.getUserRole()) {

		case CUSTOMER -> {
			user = new Customer();
		}

		case SELLER -> {
			user = new Seller();
		}

		default -> {
			throw new InvalidUserRoleException("Please enter proper user role");
		}
		}

		user.setEmail(userRequest.getEmail());
		user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		user.setUserRole(userRequest.getUserRole());

		return (T) user;
	}

	UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName()).email(user.getEmail())
				.isEmailVerified(user.isEmailVerified()).isDeleted(user.isDeleted()).build();

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest) {

		User user = null;
		String OTP = null;

		if (userRepo.existsByEmail(userRequest.getEmail()) == false) {

			OTP = generateOTP();

			user = mapToUser(userRequest);
			user.setUserName(userRequest.getEmail().split("@")[0]);

			userCacheStore.add(userRequest.getEmail(), user);
			otpCacheStore.add(userRequest.getEmail(), OTP);

			try {
				sendOTPToMail(user, OTP);
			} catch (MessagingException e) {
				log.error("The Email Address doesnt exist");
				e.printStackTrace();
			}

		} else {
			throw new UserAlreadyRegisteredException("User already registered with the given Email");
		}

//		if (user instanceof Seller)
//			user = sellerRepo.save((Seller) user);
//
//		else if (user instanceof Customer)
//			user = customerRepo.save((Customer) user);

		structure.setStatus(HttpStatus.ACCEPTED.value()).setMessage("Please Verify mailId using OTP sent " + OTP)
				.setData(mapToUserResponse(user));

		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.ACCEPTED);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OTPModel otpModel) {

		User user = userCacheStore.get(otpModel.getEmail());
		String otp = otpCacheStore.get(otpModel.getEmail());

		if (otp == null)
			throw new OtpExpiredException("OTP Expired register again");
		if (user == null)
			throw new SessionExpiredException("Session expired register again");
		if (!otp.equals(otpModel.getOtp()))
			throw new InvalidOtpException("Please Enter correct OTP");

		user.setEmailVerified(true);
		userRepo.save(user);

		try {
			confirmMail(user);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		structure.setStatus(HttpStatus.CREATED.value()).setMessage("User registered successfully")
				.setData(mapToUserResponse(user));

		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(String accessToken, String refreshToken,
			AuthRequest authRequest, HttpServletResponse response) {

		if (accessToken == null && refreshToken == null) {
			String username = authRequest.getEmail().split("@")[0];

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,
					authRequest.getPassword());
			Authentication authentication = authenticationManager.authenticate(token);
			if (!authentication.isAuthenticated())
				throw new UsernameNotFoundException("Failed to authenticate the user");

			return userRepo.findByUserName(username).map(user -> {
				grantAccess(response, user);
				return ResponseEntity.ok(authStructure.setStatus(HttpStatus.OK.value()).setMessage("success")
						.setData(mapToAuthResponse(user)));

			}).orElseThrow(() -> new UsernameNotFoundException("user name not found"));
		} else
			throw new UserAlreadyLoggedInException("User already Logged In");

	}

	@Override
	public ResponseEntity<ResponseStructure<HttpServletResponse>> logoutTraditional(HttpServletRequest req,
			HttpServletResponse resp) {

		String rt = null;
		String at = null;
		Cookie[] cookies = req.getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("rt"))
				rt = cookie.getValue();
			if (cookie.getName().equals("at"))
				at = cookie.getValue();
		}
		accessTRepo.findByToken(at).ifPresent(accessToken -> {
			accessToken.setBlocked(true);
			accessTRepo.save(accessToken);
		});
		refreshTRepo.findByToken(rt).ifPresent(refreshToken -> {
			refreshToken.setBlocked(true);
			refreshTRepo.save(refreshToken);
		});

		resp.addCookie(cookieManager.invalidate(new Cookie(at, "")));
		resp.addCookie(cookieManager.invalidate(new Cookie(rt, "")));

		rs.setData(resp);
		rs.setMessage("Cookie invalidated");
		rs.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<ResponseStructure<HttpServletResponse>>(rs, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> logout(String at, String rt, HttpServletResponse resp) {
		System.out.println("hitting");
		if (at == null && rt == null)
			throw new UserNotLoggedInException("Please LogIn");

		accessTRepo.findByToken(at).ifPresent(accessToken -> {
			System.out.println(accessToken);
			accessToken.setBlocked(true);
			accessTRepo.save(accessToken);
		});
		refreshTRepo.findByToken(rt).ifPresent(refreshToken -> {
			refreshToken.setBlocked(true);
			refreshTRepo.save(refreshToken);
		});

		resp.addCookie(cookieManager.invalidate(new Cookie("at", "")));
		resp.addCookie(cookieManager.invalidate(new Cookie("rt", "")));

		srs.setMessage("Logged Out successfully");
		srs.setStatus(HttpStatus.OK.value());
		return new ResponseEntity<SimpleResponseStructure>(srs, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> refreshLoginAndTokenRotate(String at, String rt,
			HttpServletResponse response) {

		String user = SecurityContextHolder.getContext().getAuthentication().getName();

		if (rt == null)
			throw new UserNotLoggedInException("User not logged in expired token");

		if (user == null)
			throw new UserNotLoggedInException("user not logged in");

		if (at != null) {
			accessTRepo.findByToken(at).ifPresent(act -> {
				act.setBlocked(true);
				accessTRepo.save(act);
			});
		}
		if (rt != null) {
			refreshTRepo.findByToken(rt).ifPresent(rft -> {
				rft.setBlocked(true);
				refreshTRepo.save(rft);
			});
		}
		return userRepo.findByUserName(user).map(userdb -> {
			grantAccess(response, userdb);

			srs.setMessage("refresh the token");
			srs.setStatus(HttpStatus.OK.value());

			return new ResponseEntity<SimpleResponseStructure>(srs, HttpStatus.OK);
			
		}).orElseThrow(() -> new UserNotFoundException("user doesnt exist"));

	}

	private AuthResponse mapToAuthResponse(User user) {
		return AuthResponse.builder().userId(user.getUserId()).username(user.getUserName()).isAuthenticated(true)
				.role(user.getUserRole().name())
				.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
				.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds)).build();

	}

	@Async
	private void sendMail(MessageStructure message) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSentDate());
		helper.setText(message.getText(), true);
		javaMailSender.send(mimeMessage);
	}

	private void sendOTPToMail(User user, String otp) throws MessagingException {
		sendMail(MessageStructure.builder().to(user.getEmail()).subject("Complete Your Registeration to Flipkart ")
				.sentDate(new Date())
				.text("hey, " + user.getUserName() + "Good to see you interested in FlipKart, "
						+ "Complete your Registeration using the OTP <br>" + "<h1>" + otp + "</h1><br>"
						+ "Note: the OTP expires in 1 minute" + "<br><br>" + "with best regards<br>" + "FlipKart")
				.build());
	}

	private void confirmMail(User user) throws MessagingException {
		sendMail(MessageStructure
				.builder().to(user.getEmail()).subject("Registeration complete ").sentDate(new Date()).text("Namaste, "
						+ user.getUserName() + "You have been registerd successfully to the <h1>FlipKart</h1>")
				.build());
	}

	private String generateOTP() {

		// String str = "" + (int) (Math.random() * 900000) + 100000;
		// return str;

		return String.valueOf(new Random().nextInt(100000, 999999));
	}

	private void grantAccess(HttpServletResponse response, User user) {
		// gennerating access and refresh tokens
		String accessToken = jwtService.generateAccessToken(user.getUserName());
		String refreshToken = jwtService.generateRefreshToken(user.getUserName());

		// adding access anf refresh tokens cookies to the response
		response.addCookie(cookieManager.configure(new Cookie("at", accessToken), accessExpiryInSeconds));
		response.addCookie(cookieManager.configure(new Cookie("rt", refreshToken), refreshExpiryInSeconds));

		// saving the access and refresh cookie into the database
		accessTRepo.save(AccessToken.builder().token(accessToken).isBlocked(false)
				.expiration(LocalDateTime.now().plusMinutes(accessExpiryInSeconds)).build());

		refreshTRepo.save(RefreshToken.builder().token(refreshToken).isBlocked(false)
				.expiration(LocalDateTime.now().plusMinutes(refreshExpiryInSeconds)).build());
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> revokeAll() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(username);

		return userRepo.findByUserName(username).map(user -> {

			blockAccessToken(accessTRepo.findByUserAndIsBlocked(user, false));
			blockRefreshToken(refreshTRepo.findByUserAndIsBlocked(user, false));

			srs.setMessage(" Revoked From all devices");
			srs.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<SimpleResponseStructure>(srs, HttpStatus.OK);
		}).orElseThrow(() -> new UsernameNotFoundException("User not found"));

	}

	@Override
	public ResponseEntity<SimpleResponseStructure> revokeOther(String accessToken, String refreshToken,
			HttpServletResponse response) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		userRepo.findByUserName(username).ifPresent(user -> {
			blockAccessToken(accessTRepo.findAllByUserAndIsBlockedAndTokenNot(user, false, accessToken));
			blockRefreshToken(refreshTRepo.findAllByUserAndIsBlockedAndTokenNot(user, false, refreshToken));
		});
		srs.setMessage(" Revoked other devices ");
		srs.setStatus(HttpStatus.OK.value());

		return new ResponseEntity<SimpleResponseStructure>(srs, HttpStatus.OK);

	}

	private void blockAccessToken(List<AccessToken> accessToken) {
		accessToken.forEach(at -> {
			at.setBlocked(true);
			accessTRepo.save(at);
		});
	}

	private void blockRefreshToken(List<RefreshToken> refreshToken) {
		refreshToken.forEach(rt -> {
			rt.setBlocked(true);
			refreshTRepo.save(rt);
		});
	}

}
