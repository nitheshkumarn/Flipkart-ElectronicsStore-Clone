package com.flipkartapp.es.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer>{

}
