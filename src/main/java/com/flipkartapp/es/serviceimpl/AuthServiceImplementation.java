package com.flipkartapp.es.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.flipkartapp.es.Exception.InvalidUserRoleException;
import com.flipkartapp.es.Exception.UserAlreadyRegisteredException;
import com.flipkartapp.es.entity.Customer;
import com.flipkartapp.es.entity.Seller;
import com.flipkartapp.es.entity.User;
import com.flipkartapp.es.repository.CustomerRepository;
import com.flipkartapp.es.repository.SellerRepository;
import com.flipkartapp.es.repository.UserRepository;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.service.AuthService;
import com.flipkartapp.es.util.ResponseStructure;

@Service
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

		return (T) user;
	}

	UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().userId(user.getUserId()).userName(user.getUserName()).email(user.getEmail())
				.isEmailVerified(user.isEmailVerified()).isDeleted(user.isDeleted()).build();

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest) {

		User user = null;

		if (userRepo.existsByEmailAndIsEmailVerified(userRequest.getEmail(), true) == false) {
			if (userRepo.existsByEmail(userRequest.getEmail()) == false) {
				user = mapToUser(userRequest);
				user.setUserName(userRequest.getEmail().split("@")[0]);
			} else {
				//
			}
		} else
			throw new UserAlreadyRegisteredException("User Already Registered");

		if (user instanceof Seller)
			user = sellerRepo.save((Seller) user);

		else if (user instanceof Customer)
			user = customerRepo.save((Customer) user);

		structure.setStatus(HttpStatus.ACCEPTED.value()).setMessage("Please Verify mailId using OTP sent")
				.setData(mapToUserResponse(user));

		return new ResponseEntity<ResponseStructure<UserResponse>>(structure, HttpStatus.ACCEPTED);
	}

}
