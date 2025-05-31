package com.PickOne.domain.consent.dto;

import com.PickOne.domain.consent.model.domain.Consent;

import java.time.LocalDateTime;

/**
 * 사용자 동의 이력 응답 DTO
 */
public record ConsentResponseDto(
        Long userId,
        Long termsId,
        boolean consented,
        LocalDateTime consentDate
) {
    public static ConsentResponseDto from(Consent consent) {
        return new ConsentResponseDto(
                consent.getUserId(),
                consent.getTermsId(),
                consent.isConsented(),
                consent.getConsentDate()
        );
    }
}
