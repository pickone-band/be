package com.PickOne.domain.consent.model.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 동의 이력 도메인
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Consent {
    private final Long userId;
    private final Long termsId;
    private final boolean consented;
    private final LocalDateTime consentDate;
}