package com.flipkartapp.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
