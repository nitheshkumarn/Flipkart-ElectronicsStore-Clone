package com.flipkartapp.es.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkartapp.es.requestdto.AuthRequest;
import com.flipkartapp.es.requestdto.OTPModel;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.AuthResponse;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.service.AuthService;
import com.flipkartapp.es.util.ResponseStructure;
import com.flipkartapp.es.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor

@RequestMapping("/api/v1")
public class AuthController {

	private AuthService as;

	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UserResponse>> registerUser(@RequestBody UserRequest userRequest) {
		return as.registerUser(userRequest);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(@RequestBody OTPModel otpModel) {
		return as.verifyOTP(otpModel);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<AuthResponse>> login(@RequestBody AuthRequest authRequest,
			HttpServletResponse response) {
		return as.login(authRequest,response);
	}
	
	@PostMapping("/logout-traditional")
	public ResponseEntity<ResponseStructure<HttpServletResponse>> logoutTraditional(HttpServletResponse resp, HttpServletRequest req){
		return as.logoutTraditional(req,resp);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ResponseStructure<SimpleResponseStructure>> logout(@CookieValue(name = "at", required = false) String accessToken, @CookieValue(name = "rt", required = false) String refreshToken, HttpServletResponse resp ){
		System.out.println("logging out");
		return as.logout(accessToken, refreshToken, resp);
	}
	
}
