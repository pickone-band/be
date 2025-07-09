package com.pickone.global.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 결과 DTO")
public record AuthResult(
    @Schema(description = "엑세스 토큰", example = "jwtAccessToken") String accessToken,
    @Schema(description = "리프레시 토큰", example = "jwtRefreshToken") String refreshToken,
    @Schema(description = "이메일", example = "user@example.com") String email
) {
}
