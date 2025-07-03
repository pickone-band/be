package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Mbti;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequestDto(
    @NotBlank
    String nickname,
    Mbti mbti
) {}