package com.flipkartapp.es.exceptionhandler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flipkartapp.es.Exception.InvalidUserRoleException;
import com.flipkartapp.es.Exception.UserAlreadyRegisteredException;

@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler {
	
	public ResponseEntity<Object> structure(HttpStatus status, String message, Object rootCause) {
		return new ResponseEntity<Object>(Map.of("status", status.value(), "message", message, "root cause", rootCause),
				status);
	}
	
	@ExceptionHandler(UserAlreadyRegisteredException.class)
	public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyRegisteredException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "User Already Registered with the email");
	}
	
	@ExceptionHandler(InvalidUserRoleException.class)
	public ResponseEntity<Object> handleInvalidUserRoleException(InvalidUserRoleException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "Enter proper user role");
	}

}
