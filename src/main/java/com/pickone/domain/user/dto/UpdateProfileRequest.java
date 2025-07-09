package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Mbti;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 수정 요청")
public record UpdateProfileRequest(
    @Schema(description = "닉네임", example = "cool_user") String nickname,
    @Schema(description = "MBTI", example = "ENTP") Mbti mbti,
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg") String profileImageUrl,
    @Schema(description = "소개", example = "안녕하세요! 음악을 사랑하는 유저입니다.") String introduction
) {}
