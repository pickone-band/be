package com.PickOne.global.verification.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank(message = "토큰은 필수입니다.")
        String token
) {}