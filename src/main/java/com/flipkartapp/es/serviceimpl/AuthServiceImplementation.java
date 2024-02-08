package com.flipkartapp.es.serviceimpl;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipkartapp.es.Exception.InvalidOtpException;
import com.flipkartapp.es.Exception.InvalidUserRoleException;
import com.flipkartapp.es.Exception.OtpExpiredException;
import com.flipkartapp.es.Exception.SessionExpiredException;
import com.flipkartapp.es.Exception.UserAlreadyRegisteredException;
import com.flipkartapp.es.cache.CacheStore;
import com.flipkartapp.es.entity.Customer;
import com.flipkartapp.es.entity.Seller;
import com.flipkartapp.es.entity.User;
import com.flipkartapp.es.repository.CustomerRepository;
import com.flipkartapp.es.repository.SellerRepository;
import com.flipkartapp.es.repository.UserRepository;
import com.flipkartapp.es.requestdto.OTPModel;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.service.AuthService;
import com.flipkartapp.es.util.MessageStructure;
import com.flipkartapp.es.util.ResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImplementation implements AuthService {

	@Autowired
	UserRepository userRepo;

	@Autowired
	SellerRepository sellerRepo;

	@Autowired
	CustomerRepository customerRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	ResponseStructure<UserResponse> structure;

	@Autowired
	CacheStore<String> otpCacheStore;

	@Autowired
	CacheStore<User> userCacheStore;

	@Autowired
	private JavaMailSender javaMailSender;

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

			try {
				confirmMail(user);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
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
		structure.setStatus(HttpStatus.CREATED.value()).setMessage("User registered successfully")
				.setData(mapToUserResponse(user));

		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.CREATED);
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
}
