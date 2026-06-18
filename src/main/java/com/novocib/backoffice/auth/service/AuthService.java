package com.novocib.backoffice.auth.service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final UserRepository userRepository;

  public AuthService(AuthenticationManager authenticationManager,
                     JwtService jwtService,
                     RefreshTokenService refreshTokenService,
                     UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.refreshTokenService = refreshTokenService;
    this.userRepository = userRepository;
  }

  public LoginResponse login(LoginRequest request) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    UserDetails userDetails = (UserDetails) auth.getPrincipal();
    String accessToken = jwtService.generateAccessToken(userDetails);

    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow();

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

    // you can set refresh token in HttpOnly cookie here
    // or return it in body if you prefer (less secure)

    return new LoginResponse(accessToken, "Bearer", 900_000L); // example 15 min
  }

  public LoginResponse refresh(String refreshTokenValue) {
    RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenValue)
        .map(refreshTokenService::verifyExpiration)
        .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

    User user = refreshToken.getUser();
    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword(),
        user.isEnabled(), true, true, true,
        user.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
    );

    String newAccessToken = jwtService.generateAccessToken(userDetails);

    return new LoginResponse(newAccessToken, "Bearer", 900_000L);
  }
}
