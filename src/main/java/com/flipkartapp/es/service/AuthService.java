package com.flipkartapp.es.service;

import org.springframework.http.ResponseEntity;

import com.flipkartapp.es.requestdto.AuthRequest;
import com.flipkartapp.es.requestdto.OTPModel;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.AuthResponse;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.util.ResponseStructure;
import com.flipkartapp.es.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerUser(UserRequest userRequest);
	
	ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OTPModel otpmODEL);

	ResponseEntity<ResponseStructure<AuthResponse>> login(AuthRequest authRequest,HttpServletResponse response);

	ResponseEntity<ResponseStructure<HttpServletResponse>> logoutTraditional(HttpServletRequest req, HttpServletResponse resp);

	 ResponseEntity<SimpleResponseStructure> logout(String accessToken, String refreshToken,
			HttpServletResponse resp);

	ResponseEntity<SimpleResponseStructure> revokeOther(String accessToken, String refreshToken,
			HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure> revokeAll();

}
