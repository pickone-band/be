package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.vo.UserPreference;
import com.pickone.domain.userInstrument.dto.UserInstrumentResponse;
import com.pickone.domain.userInstrument.entity.UserInstrument;
import com.pickone.global.common.enums.Mbti;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "사용자 응답")
public record UserResponse(
    @Schema(description = "사용자 ID", example = "1") Long id,
    @Schema(description = "닉네임", example = "username") String nickname,
    @Schema(description = "이메일", example = "user@example.com") String email,
    @Schema(description = "생년월일", example = "2000-01-01") LocalDate birthDate,
    @Schema(description = "성별", example = "FEMALE") Gender gender,
    @Schema(description = "MBTI", example = "INFP") Mbti mbti,
    @Schema(description = "활성 상태", example = "true") boolean isActive,
    @Schema(description = "인증 여부", example = "false") boolean isVerified,
    @Schema(description = "잠금 여부", example = "false") boolean isLocked,
    @Schema(description = "2단계 인증 사용 여부", example = "false") boolean twoFactorEnabled,
    @Schema(description = "선호 장르 정보", implementation = UserPreferenceResponse.class)
    UserPreferenceResponse preference,
    @Schema(description = "악기 목록", implementation = UserInstrumentResponse.class)
    List<UserInstrumentResponse> instruments,
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    String profileImageUrl,
    @Schema(description = "소개", example = "안녕하세요!") String introduction
) {}
