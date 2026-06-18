package com.novocib.backoffice.auth.service;

import com.novocib.backoffice.auth.domain.RefreshToken;
import com.novocib.backoffice.auth.domain.User;
import com.novocib.backoffice.auth.dto.LoginRequest;
import com.novocib.backoffice.auth.dto.LoginResult;
import com.novocib.backoffice.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

  public LoginResult login(LoginRequest request) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.email(), request.password())
    );

    UserDetails userDetails = (UserDetails) auth.getPrincipal();
    String accessToken = jwtService.generateAccessToken(userDetails);

    User user = userRepository.findByEmail(userDetails.getUsername())
        .orElseThrow();

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

    return new LoginResult(
        accessToken,
        "Bearer",
        jwtService.getAccessTokenExpirationMs(),
        refreshToken.getToken(),
        refreshTokenService.getRefreshTokenExpirationMs()
    );
  }

  public LoginResult refresh(String refreshTokenValue) {
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
    RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

    return new LoginResult(
        newAccessToken,
        "Bearer",
        jwtService.getAccessTokenExpirationMs(),
        newRefreshToken.getToken(),
        refreshTokenService.getRefreshTokenExpirationMs()
    );
  }

  public void revokeRefreshToken(String refreshTokenValue) {
    refreshTokenService.revokeRefreshToken(refreshTokenValue);
  }
}
