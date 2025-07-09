package com.pickone.global.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "동의 약관 요청")
public record ConsentAgreementRequest(
    @NotNull(message = "약관 ID는 필수입니다.")
    @Schema(description = "약관 ID", example = "1") Long termId,
    @NotNull(message = "동의 여부는 필수입니다.")
    @Schema(description = "동의 여부", example = "true") Boolean consented
) {}