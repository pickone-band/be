package com.PickOne.global.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email
) {}