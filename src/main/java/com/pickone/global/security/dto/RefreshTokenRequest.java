package com.pickone.global.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "리프레시 토큰 요청")
public record RefreshTokenRequest(
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    @Schema(description = "Refresh Token", example = "refresh.jwt.token")
    String refreshToken
) {}