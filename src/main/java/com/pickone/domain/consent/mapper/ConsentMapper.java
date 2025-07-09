package com.pickone.domain.consent.mapper;

import com.pickone.domain.consent.dto.ConsentResponse;
import com.pickone.domain.consent.entity.Consent;
import org.springframework.stereotype.Component;

@Component
public class ConsentMapper {

  public ConsentResponse toDto(Consent entity) {
    if (entity == null) {
      return null;
    }

    return new ConsentResponse(
        entity.getId(),
        entity.getTerm().getId(),
        entity.getConsented(),
        entity.getConsentedAt()
    );
  }
}
