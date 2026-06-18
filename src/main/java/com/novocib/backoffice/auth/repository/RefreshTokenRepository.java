package com.novocib.backoffice.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.novocib.backoffice.auth.domain.RefreshToken;
import com.novocib.backoffice.auth.domain.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
Optional<RefreshToken> findByToken(String token);
  void deleteByUser(User user);
}
