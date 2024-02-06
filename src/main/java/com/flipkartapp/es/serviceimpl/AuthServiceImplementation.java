package com.flipkartapp.es.serviceimpl;

import org.springframework.http.ResponseEntity;

import com.flipkartapp.es.entity.User;
import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.service.AuthService;
import com.flipkartapp.es.util.ResponseStructure;

public class AuthServiceImplementation implements AuthService {
	
	User mapToUser(UserRequest userRequest) {
		return User.builder().email(userRequest.getEmail()).password(userRequest.getPassword()).build();
}
	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest) {
	return null;
	}

}
