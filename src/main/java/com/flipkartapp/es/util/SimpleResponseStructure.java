package com.flipkartapp.es.util;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class SimpleResponseStructure {
	
	private Integer status;
	private String message;
}
