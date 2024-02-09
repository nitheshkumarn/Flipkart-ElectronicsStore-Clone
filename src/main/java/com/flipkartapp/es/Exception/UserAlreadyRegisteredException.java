package com.flipkartapp.es.Exception;

import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class UserAlreadyRegisteredException extends RuntimeException {
	
	private String message;

}
