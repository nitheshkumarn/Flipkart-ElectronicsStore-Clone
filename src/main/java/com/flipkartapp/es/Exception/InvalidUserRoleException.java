package com.flipkartapp.es.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidUserRoleException extends RuntimeException {

	private String message;
}
