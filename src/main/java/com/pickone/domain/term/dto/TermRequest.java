package com.pickone.domain.term.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "약관 생성/수정 요청")
public record TermRequest(
    @Schema(description = "약관 제목", example = "개인정보 처리방침") String title,
    @Schema(description = "약관 내용", example = "이 약관은 개인정보의 수집 및 이용에 대해 설명합니다.") String content,
    @Schema(description = "약관 버전", example = "v1.0") String version,
    @Schema(description = "필수 여부", example = "true") boolean required,
    @Schema(description = "시행 일자", example = "2024-01-01T00:00:00") LocalDateTime effectiveDate
) {}
