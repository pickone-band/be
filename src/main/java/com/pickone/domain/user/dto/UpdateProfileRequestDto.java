package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.global.common.enums.Mbti;
import java.time.LocalDate;

public record UpdateProfileRequestDto(
    String nickname,
    LocalDate birthDate,
    Gender gender,
    Mbti mbti
) {}