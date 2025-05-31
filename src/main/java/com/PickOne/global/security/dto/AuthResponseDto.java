package com.PickOne.global.security.dto;

import com.PickOne.domain.user.dto.UserResponse;
import com.PickOne.domain.user.mapper.UserMapper;

public record AuthResponseDto(String accessToken, String refreshToken, UserResponse user) {

    public static AuthResponseDto of(AuthResult result) {
        return new AuthResponseDto(
                result.accessToken(),
                result.refreshToken(),
                UserMapper.toResponse(result.user())
        );
    }
}
