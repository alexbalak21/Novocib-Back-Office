package com.novocib.backoffice.auth.service;

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

    // optional: delete old tokens for this user
    refreshTokenRepository.deleteByUser(user);

    RefreshToken token = new RefreshToken();
    token.setUser(user);
    token.setToken(UUID.randomUUID().toString());
    token.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));

    return refreshTokenRepository.save(token);
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
}
