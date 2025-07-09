package com.pickone.domain.consent.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "약관 동의 요청")
public record ConsentRequest(
    @Schema(description = "약관 ID", example = "1") Long termId,
    @Schema(description = "동의 여부", example = "true") boolean consented
) {

}