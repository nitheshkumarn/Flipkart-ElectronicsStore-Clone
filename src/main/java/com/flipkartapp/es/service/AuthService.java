package com.flipkartapp.es.service;

import org.springframework.http.ResponseEntity;

import com.flipkartapp.es.requestdto.AuthRequest;
import com.flipkartapp.es.requestdto.OTPModel;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.AuthResponse;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.util.ResponseStructure;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest);
	
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OTPModel otpmODEL);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse response);

}
