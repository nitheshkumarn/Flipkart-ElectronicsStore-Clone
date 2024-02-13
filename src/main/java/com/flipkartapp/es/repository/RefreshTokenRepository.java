package com.flipkartapp.es.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkartapp.es.entity.RefreshToken;
import com.flipkartapp.es.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	public Optional<RefreshToken> findByToken(String rt);

	public List<RefreshToken> findByExpirationBefore(LocalDateTime expiry);

	public List<RefreshToken> findByUserAndIsBlocked(User user, boolean b);

	public List<RefreshToken> findAllByUserAndIsBlockedAndTokenNot(User user, boolean b, String refreshToken);

}
