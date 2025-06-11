package com.PickOne.global.security.dto;

import com.PickOne.domain.user.model.domain.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record SignupRequestDto(

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
        String password,

        @NotBlank(message = "닉네임은 필수입니다")
        String nickname,

        @NotNull(message = "생년월일은 필수입니다")
        LocalDate birthDate,

        @NotNull(message = "성별은 필수입니다")
        Gender gender,

        @NotEmpty(message = "약관 동의 내역은 비어 있을 수 없습니다.")
        List<ConsentAgreementDto> agreements

) {}