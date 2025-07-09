package com.pickone.global.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 응답")
public record AuthResponse(
    @Schema(description = "Access Token", example = "access.jwt.token") String accessToken,
    @Schema(description = "Refresh Token", example = "refresh.jwt.token") String refreshToken,
    @Schema(description = "사용자 이메일", example = "user@example.com") String email
) {
}