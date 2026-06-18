package com.novocib.backoffice.auth.dto;

public record LoginResult(
    String accessToken,
    String tokenType,
    long accessTokenExpiresIn,
    String refreshToken,
    long refreshTokenExpiresIn) {
}
