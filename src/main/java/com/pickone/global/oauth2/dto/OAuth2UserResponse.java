package com.pickone.global.oauth2.dto;

import com.pickone.domain.user.model.domain.Gender;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "OAuth2 사용자 정보 응답 DTO")
public record OAuth2UserResponse(

    @Schema(description = "이메일 주소", example = "user@example.com")
    String email,

    @Schema(description = "닉네임", example = "musician123")
    String nickname,

    @Schema(description = "성별", example = "MALE")
    Gender gender,

    @Schema(description = "생년월일", example = "1997-08-15")
    LocalDate birthDate,

    @Schema(description = "프로필 이미지 URL", example = "https://profile.com/image.png")
    String profileImageUrl
) {}
