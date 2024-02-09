package com.flipkartapp.es.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.flipkartapp.es.entity.User;
import com.flipkartapp.es.repository.UserRepository;

@Component
public class ScheduledJobs {

	@Autowired
	UserRepository userRepo;

	@Scheduled(cron = "0 0 0 * * *")
	public void deleteUser() {

		List<User> users = userRepo.findByIsEmailVerified(false);
		userRepo.deleteAll(users);
	}
}
