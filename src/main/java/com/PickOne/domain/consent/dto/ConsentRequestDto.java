package com.PickOne.domain.consent.dto;

import com.PickOne.domain.consent.model.domain.Consent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 사용자 약관 동의 요청 DTO
 */
public record ConsentRequestDto(
        @NotNull Long termsId,
        @NotNull Boolean consented
) {
    public Consent toDomain(Long userId) {
        return new Consent(
                userId,
                termsId,
                consented,
                LocalDateTime.now()
        );
    }
}
