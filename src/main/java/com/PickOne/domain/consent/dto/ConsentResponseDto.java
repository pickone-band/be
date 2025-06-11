package com.PickOne.domain.consent.dto;

import com.PickOne.domain.consent.model.entity.ConsentEntity;

import java.time.LocalDateTime;

public record ConsentResponseDto(
        Long userId,
        Long termsId,
        boolean consented,
        LocalDateTime consentDate
) {
    public static ConsentResponseDto from(ConsentEntity consent) {
        return new ConsentResponseDto(
                consent.getUser().getId(),
                consent.getTerm().getId(),
                consent.isConsented(),
                consent.getConsentDate()
        );
    }
}