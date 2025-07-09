package com.pickone.global.oauth2.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OAuth2 로그인 후 발급되는 액세스/리프레시 토큰 정보")
public record TokenResponse(
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,

    @Schema(description = "리프레시 토큰", example = "d2ViLXJlZnJlc2gtdG9rZW4=")
    String refreshToken,

    @Schema(description = "토큰 타입", example = "Bearer")
    String tokenType,

    @Schema(description = "액세스 토큰 만료 시간 (초)", example = "3600")
    long expiresIn
) {}
