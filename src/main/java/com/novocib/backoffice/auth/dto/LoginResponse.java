package com.novocib.backoffice.auth.dto;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {}