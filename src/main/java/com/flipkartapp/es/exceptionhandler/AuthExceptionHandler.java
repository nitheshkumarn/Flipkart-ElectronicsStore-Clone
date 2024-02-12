package com.flipkartapp.es.exceptionhandler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.flipkartapp.es.Exception.InvalidOtpException;
import com.flipkartapp.es.Exception.InvalidUserRoleException;
import com.flipkartapp.es.Exception.OtpExpiredException;
import com.flipkartapp.es.Exception.SessionExpiredException;
import com.flipkartapp.es.Exception.UserAlreadyRegisteredException;
import com.flipkartapp.es.Exception.UserNotLoggedInException;

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
	
	@ExceptionHandler(SessionExpiredException.class)
	public ResponseEntity<Object> handleSessionExpiredException(SessionExpiredException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "Session has been expired");
	}
	
	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<Object> handleotpExpiredException(OtpExpiredException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "OTP has expired");
	}
	
	@ExceptionHandler(InvalidOtpException.class)
	public ResponseEntity<Object> handleInvalidOtpException(InvalidOtpException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "Incorrect OTP entered");
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "Authentication of User is failed");
	}
	
	@ExceptionHandler(UserNotLoggedInException.class)
	public ResponseEntity<Object> handleUserNotLoggedIn(UserNotLoggedInException exception) {
		return structure(HttpStatus.NOT_FOUND, exception.getMessage(), "User not logged in");
	}

}
