package com.novocib.backoffice.auth.controller;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                             HttpServletResponse response) {
    LoginResponse loginResponse = authService.login(request);

    // set refresh token cookie if you want:
    // Cookie cookie = new Cookie("refreshToken", refreshTokenValue);
    // cookie.setHttpOnly(true);
    // cookie.setPath("/api/auth/refresh");
    // response.addCookie(cookie);

    return ResponseEntity.ok(loginResponse);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
    LoginResponse loginResponse = authService.refresh(request.refreshToken());
    return ResponseEntity.ok(loginResponse);
  }
}
