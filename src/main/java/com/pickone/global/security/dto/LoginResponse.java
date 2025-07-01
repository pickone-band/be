package com.pickone.global.security.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    Long userId
) {}
