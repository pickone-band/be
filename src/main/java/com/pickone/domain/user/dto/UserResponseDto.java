package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.global.common.enums.Mbti;
import java.time.LocalDate;
import java.util.List;

public record UserResponseDto(
    Long id,
    String nickname,
    String email,
    LocalDate birthDate,
    Gender gender,
    Mbti mbti,
    boolean isActive,
    boolean isVerified,
    boolean isLocked,
    boolean twoFactorEnabled,
    UserPreferenceDto preference,
    List<UserInstrumentDto> instruments
) {}
