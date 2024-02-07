package com.flipkartapp.es.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);

	boolean existsByEmailAndIsEmailVerified(String email, boolean b);

	Optional<User> findByUserName(String string);

	List<User> findByIsEmailVerified(boolean b);
	
	
	
	
}
