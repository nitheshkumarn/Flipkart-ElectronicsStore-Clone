package com.flipkartapp.es.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	public Optional<RefreshToken> findByToken(String rt);

}
