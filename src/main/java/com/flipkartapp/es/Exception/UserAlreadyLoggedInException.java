package com.flipkartapp.es.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserAlreadyLoggedInException extends RuntimeException {
private String message;
}
