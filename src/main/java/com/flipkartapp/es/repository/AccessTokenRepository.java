package com.flipkartapp.es.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer>{
	
	public Optional<AccessToken> findByToken(String at);

}
