package com.novocib.backoffice.auth.controller;

import com.novocib.backoffice.auth.dto.LoginRequest;
import com.novocib.backoffice.auth.dto.LoginResponse;
import com.novocib.backoffice.auth.dto.LoginResult;
import com.novocib.backoffice.auth.dto.RefreshTokenRequest;
import com.novocib.backoffice.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final boolean refreshCookieSecure;
  private final String refreshCookiePath;
  private final String refreshCookieName;

  public AuthController(
      AuthService authService,
      @Value("${security.jwt.refresh-token-cookie-secure:false}") boolean refreshCookieSecure,
      @Value("${security.jwt.refresh-token-cookie-path:/api/auth}") String refreshCookiePath,
      @Value("${security.jwt.refresh-token-cookie-name:refreshToken}") String refreshCookieName) {
    this.authService = authService;
    this.refreshCookieSecure = refreshCookieSecure;
    this.refreshCookiePath = refreshCookiePath;
    this.refreshCookieName = refreshCookieName;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                             HttpServletResponse response) {
    LoginResult loginResult = authService.login(request);
    ResponseCookie cookie = ResponseCookie.from(refreshCookieName, loginResult.refreshToken())
        .httpOnly(true)
        .secure(refreshCookieSecure)
        .sameSite("Strict")
        .path(refreshCookiePath)
        .maxAge(loginResult.refreshTokenExpiresIn() / 1000)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.ok(new LoginResponse(loginResult.accessToken(), loginResult.tokenType(), loginResult.accessTokenExpiresIn()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(
      @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
      @RequestBody(required = false) RefreshTokenRequest request,
      HttpServletResponse response) {

    String refreshToken = cookieRefreshToken;
    if ((refreshToken == null || refreshToken.isBlank()) && request != null) {
      refreshToken = request.refreshToken();
    }

    if (refreshToken == null || refreshToken.isBlank()) {
      return ResponseEntity.badRequest().build();
    }

    LoginResult loginResult = authService.refresh(refreshToken);
    ResponseCookie cookie = ResponseCookie.from(refreshCookieName, loginResult.refreshToken())
        .httpOnly(true)
        .secure(refreshCookieSecure)
        .sameSite("Strict")
        .path(refreshCookiePath)
        .maxAge(loginResult.refreshTokenExpiresIn() / 1000)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.ok(new LoginResponse(loginResult.accessToken(), loginResult.tokenType(), loginResult.accessTokenExpiresIn()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken,
                                     HttpServletResponse response) {
    if (refreshToken != null && !refreshToken.isBlank()) {
      authService.revokeRefreshToken(refreshToken);
    }

    ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
        .httpOnly(true)
        .secure(refreshCookieSecure)
        .sameSite("Strict")
        .path(refreshCookiePath)
        .maxAge(0)
        .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.noContent().build();
  }
}
