package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record SignupRequestDto(
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String nickname,
    @NotNull LocalDate birthDate,
    @NotNull Gender gender,
    Mbti mbti,
    List<Genre> genres
) {}
