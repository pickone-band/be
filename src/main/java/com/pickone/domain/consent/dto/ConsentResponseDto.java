package com.pickone.domain.consent.dto;

import com.pickone.domain.consent.model.entity.ConsentEntity;

import java.time.LocalDateTime;

public record ConsentResponseDto(
    Long id,
    Long termId,
    Boolean consented,
    LocalDateTime consentDate
) {
  public static ConsentResponseDto from(ConsentEntity entity) {
    return new ConsentResponseDto(
        entity.getId(),
        entity.getTerm().getId(),
        entity.isConsented(),
        entity.getConsentDate()
    );
  }
}
