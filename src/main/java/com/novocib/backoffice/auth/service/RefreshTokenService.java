package com.novocib.backoffice.auth.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.novocib.backoffice.auth.domain.RefreshToken;
import com.novocib.backoffice.auth.domain.User;
import com.novocib.backoffice.auth.repository.RefreshTokenRepository;
import com.novocib.backoffice.auth.repository.UserRepository;

@Service
public class RefreshTokenService {

  @Value("${security.jwt.refresh-token-expiration-ms}")
  private long refreshTokenExpirationMs;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  public RefreshTokenService(RefreshTokenRepository repo, UserRepository userRepo) {
    this.refreshTokenRepository = repo;
    this.userRepository = userRepo;
  }

  public RefreshToken createRefreshToken(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    refreshTokenRepository.deleteByUser(user);

    RefreshToken token = new RefreshToken();
    token.setUser(user);
    token.setToken(UUID.randomUUID().toString());
    token.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));

    return refreshTokenRepository.save(token);
  }

  public RefreshToken rotateRefreshToken(RefreshToken existingToken) {
    User user = existingToken.getUser();
    refreshTokenRepository.delete(existingToken);
    return createRefreshToken(user.getId());
  }

  public void revokeRefreshToken(String tokenValue) {
    refreshTokenRepository.findByToken(tokenValue).ifPresent(refreshTokenRepository::delete);
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().isBefore(Instant.now())) {
      refreshTokenRepository.delete(token);
      throw new RuntimeException("Refresh token expired");
    }
    return token;
  }

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public long getRefreshTokenExpirationMs() {
    return refreshTokenExpirationMs;
  }
}
