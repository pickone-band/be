package com.pickone.domain.consent.dto;

import java.time.LocalDateTime;

public record ConsentResponseDto(
    Long id,
    Long termId,
    Boolean consented,
    LocalDateTime consentDate
) {}
