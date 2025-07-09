package com.pickone.domain.user.dto;

import com.pickone.domain.consent.dto.ConsentRequest;
import com.pickone.domain.user.model.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "회원 가입 요청")
public record SignupRequest(
    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank String email,

    @Schema(description = "비밀번호", example = "P@ssw0rd")
    @NotBlank String password,

    @Schema(description = "닉네임", example = "username")
    @NotBlank String nickname,

    @Schema(description = "생년월일", example = "2000-01-01")
    @NotNull LocalDate birthDate,

    @Schema(description = "성별", example = "MALE")
    @NotNull Gender gender,

    @Schema(description = "약관 동의 목록", implementation = ConsentRequest.class)
    @NotNull List<ConsentRequest> terms
) {}
