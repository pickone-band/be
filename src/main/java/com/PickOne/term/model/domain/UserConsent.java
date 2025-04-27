package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class UserConsent {
    private Long userId;
    private Long termsId;
    private LocalDate consentDate;
    private boolean isConsented;

    private UserConsent(Long userId, Long termsId, LocalDate consentDate, boolean isConsented) {
        this.userId = userId;
        this.termsId = termsId;
        this.consentDate = consentDate;
        this.isConsented = isConsented;
    }

    public static UserConsent of(Long userId, Long termsId, LocalDate consentDate, boolean isConsented) {
        validate(userId, termsId, consentDate);
        return new UserConsent(userId, termsId, consentDate, isConsented);
    }

    private static void validate(Long userId, Long termsId, LocalDate consentDate) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 null일 수 없습니다.");
        }
        if (termsId == null) {
            throw new IllegalArgumentException("약관 ID는 null일 수 없습니다.");
        }
        if (consentDate == null) {
            throw new IllegalArgumentException("동의일은 null일 수 없습니다.");
        }
    }
}