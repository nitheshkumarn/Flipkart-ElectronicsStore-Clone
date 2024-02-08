package com.flipkartapp.es.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.flipkartapp.es.entity.User;

@Configuration
public class CacheBeanConfig {

	@Bean
	CacheStore<User> userCacheStore() {

		return new CacheStore<User>(Duration.ofMinutes(5));
		
	}
	
	@Bean
	CacheStore<String> OTPCacheStore(){
		
		return new CacheStore<>(Duration.ofMinutes(5));
	}

}
