package com.pickone.domain.consent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "약관 동의 응답")
public record ConsentResponse(
    @Schema(description = "동의 ID", example = "1") Long id,
    @Schema(description = "약관 ID", example = "101") Long termId,
    @Schema(description = "동의 여부", example = "true") boolean consented,
    @Schema(description = "동의한 시각", example = "2024-01-01T10:00:00") LocalDateTime consentedAt
) {

}
