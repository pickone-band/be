package com.pickone.domain.consent.model.mapper;

import com.pickone.domain.consent.dto.ConsentResponseDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;

public class ConsentMapper {
  public static ConsentResponseDto toDto(ConsentEntity entity) {
    return new ConsentResponseDto(
        entity.getId(),
        entity.getTerm().getId(),
        entity.getConsented(),
        entity.getConsentedAt() // ConsentEntity에서 consentedAt 필드명으로 통일
    );
  }
}
