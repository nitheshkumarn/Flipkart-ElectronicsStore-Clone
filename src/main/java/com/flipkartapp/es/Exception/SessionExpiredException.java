package com.flipkartapp.es.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionExpiredException extends RuntimeException {
private String message;
}
