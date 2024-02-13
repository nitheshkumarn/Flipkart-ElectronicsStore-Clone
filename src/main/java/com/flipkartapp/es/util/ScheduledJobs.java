package com.flipkartapp.es.util;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flipkartapp.es.entity.AccessToken;
import com.flipkartapp.es.entity.RefreshToken;
import com.flipkartapp.es.entity.User;
import com.flipkartapp.es.repository.AccessTokenRepository;
import com.flipkartapp.es.repository.RefreshTokenRepository;
import com.flipkartapp.es.repository.UserRepository;

@Component
public class ScheduledJobs {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	AccessTokenRepository accessTRepo;
	
	@Autowired
	RefreshTokenRepository refreshTRepo;

	@Scheduled(cron = "0 0 0 * * *")
	public void deleteUser() {

		List<User> users = userRepo.findByIsEmailVerified(false);
		userRepo.deleteAll(users);
	}
	
	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpiredAccessToken() {
		List<AccessToken> accessTokens = accessTRepo.findByExpirationBefore(LocalDateTime.now());
		accessTRepo.deleteAll(accessTokens);	
	}
	
	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpiredRefreshToken() {
		List<RefreshToken> refreshTokens = refreshTRepo.findByExpirationBefore(LocalDateTime.now());
		refreshTRepo.deleteAll(refreshTokens);	
	}
}
