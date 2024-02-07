package com.flipkartapp.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);

	boolean existsByEmailAndIsEmailVerified(String email, boolean b);

	Object findByUserName(String string);
	
	
}
