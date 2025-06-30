package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;

import java.util.List;

public record UserUpdateRequestDto(
    String nickname,
    String profileImage,
    Mbti mbti,
    List<InstrumentInfoDto> instruments,
    List<Genre> genres,
    String newPassword  // 선택적 비밀번호 변경 필드
) {}