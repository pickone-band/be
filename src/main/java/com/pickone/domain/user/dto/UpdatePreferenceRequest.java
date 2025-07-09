package com.pickone.domain.user.dto;

import com.pickone.global.common.enums.Genre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자 선호 장르 수정 요청")
public record UpdatePreferenceRequest(
    @Schema(description = "선호 장르 목록 (최대 8개)", example = "[\"ROCK\", \"POP\", \"JAZZ\"]")
    List<Genre> genres
) {}
