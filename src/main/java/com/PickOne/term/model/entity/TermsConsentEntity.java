package com.PickOne.term.model.entity;

import com.PickOne.common.entity.BaseEntity;
import com.PickOne.term.model.domain.TermConsent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms_consent",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "terms_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsConsentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "terms_id", nullable = false)
    private Long termsId;

    @Column(name = "consent_date", nullable = false)
    private LocalDateTime consentDate;

    @Column(name = "consented", nullable = false)
    private boolean consented;

    // 생성자
    private TermsConsentEntity(Long userId, Long termsId, LocalDateTime consentDate, boolean consented) {
        this.userId = userId;
        this.termsId = termsId;
        this.consentDate = consentDate;
        this.consented = consented;
    }

    // 도메인 모델에서 엔티티로 변환
    public static TermsConsentEntity from(TermConsent termsConsent) {
        return new TermsConsentEntity(
                termsConsent.getUserId(),
                termsConsent.getTermsId(),
                termsConsent.getConsentDate(),
                termsConsent.isAccepted()
        );
    }

    // 엔티티에서 도메인 모델로 변환
    public TermConsent toDomain() {
        return TermConsent.of(
                userId,
                termsId,
                consentDate,
                consented
        );
    }

    // 동의 상태 업데이트
    public void updateConsent(boolean consented, LocalDateTime consentDate) {
        this.consented = consented;
        this.consentDate = consentDate;
    }
}