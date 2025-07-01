package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.domain.Gender;
import java.time.LocalDate;

public record UserResponseDto(
    Long id,
    String nickname,
    String email,
    LocalDate birthDate,
    Gender gender,
    boolean isActive,
    boolean isVerified,
    UserPreferenceDto preference // 내장값 Dto로 반환
) {}
