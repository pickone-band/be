package com.pickone.global.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginResponse(
    @Schema(example = "jwtAccessToken") String accessToken,
    @Schema(example = "jwtRefreshToken") String refreshToken,
    @Schema(example = "123") Long userId
) {}