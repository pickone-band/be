package com.PickOne.domain.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"userId", "termsId"})
public class TermConsent {

    private Long userId;
    private Long termsId;
    private LocalDateTime consentDate;
    private boolean consented;

    private TermConsent(Long userId, Long termsId, LocalDateTime consentDate, boolean consented) {
        this.userId = userId;
        this.termsId = termsId;
        this.consentDate = consentDate;
        this.consented = consented;
    }

    public static TermConsent of(Long userId, Long termsId, LocalDateTime consentDate, boolean consented) {
        validate(userId, termsId, consentDate);
        return new TermConsent(userId, termsId, consentDate, consented);
    }

    private static void validate(Long userId, Long termsId, LocalDateTime consentDate) {
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

    public TermConsent updateConsent(boolean consented) {
        if (this.consented == consented) {
            return this;
        }

        return new TermConsent(
                this.userId,
                this.termsId,
                LocalDateTime.now(),
                consented
        );
    }

    public TermConsent accept() {
        return updateConsent(true);
    }

    public TermConsent withdraw() {
        return updateConsent(false);
    }

    public boolean isAccepted() {
        return this.consented;
    }
}