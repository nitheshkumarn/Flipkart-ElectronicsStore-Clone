package com.flipkartapp.es.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.flipkartapp.es.requestdto.OTPModel;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.service.AuthService;
import com.flipkartapp.es.util.ResponseStructure;

@RestController
public class AuthController {
	
	@Autowired
	AuthService as;
	
	
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest userRequest) {
		return as.registerUser(userRequest);
}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OTPModel otpModel){
		return as.verifyOTP(otpModel);
	}
}
