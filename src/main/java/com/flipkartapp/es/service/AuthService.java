package com.flipkartapp.es.service;

import org.springframework.http.ResponseEntity;

import com.flipkartapp.es.requestdto.UserRequest;
import com.flipkartapp.es.responsedto.UserResponse;
import com.flipkartapp.es.util.ResponseStructure;

public interface AuthService {

	ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest);

}
