package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Mbti;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequestDto(
    String nickname,
    Mbti mbti,
    String introduction
) {}