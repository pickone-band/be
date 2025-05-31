package com.PickOne.domain.consent.mapper;

import com.PickOne.domain.consent.dto.ConsentResponseDto;
import com.PickOne.domain.consent.model.domain.Consent;
import com.PickOne.domain.consent.model.entity.ConsentEntity;

public class ConsentMapper {

    public static Consent toDomain(ConsentEntity entity) {
        return new Consent(
                entity.getUserId(),
                entity.getTermsId(),
                entity.isConsented(),
                entity.getConsentDate()
        );
    }

    public static ConsentEntity toEntity(Consent consent) {
        return new ConsentEntity(
                null,
                consent.getUserId(),
                consent.getTermsId(),
                consent.isConsented(),
                consent.getConsentDate()
        );
    }

    public static ConsentResponseDto toResponse(Consent consent) {
        return ConsentResponseDto.from(consent);
    }
}
