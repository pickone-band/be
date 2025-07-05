package com.pickone.domain.user.dto;

import com.pickone.domain.consent.dto.ConsentRequestDto;
import com.pickone.domain.user.model.domain.Gender;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record SignupRequestDto(

    @NotBlank
    String email,

    @NotBlank
    String password,

//    @NotBlank
//    String passwordConfirm,

    @NotBlank
    String nickname,

    @NotNull
    LocalDate birthDate,

    @NotNull
    Gender gender,

    @NotNull
    List<ConsentRequestDto> terms
) {}