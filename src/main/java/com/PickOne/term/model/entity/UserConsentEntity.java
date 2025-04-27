package com.PickOne.term.model.entity;

import com.PickOne.common.entity.BaseEntity;
import com.PickOne.term.model.domain.UserConsent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "user_consent",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "terms_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserConsentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "terms_id", nullable = false)
    private Long termsId;

    @Column(name = "consent_date", nullable = false)
    private LocalDate consentDate;

    @Column(name = "is_consented", nullable = false)
    private boolean isConsented;

    // 생성자
    private UserConsentEntity(Long userId, Long termsId, LocalDate consentDate, boolean isConsented) {
        this.userId = userId;
        this.termsId = termsId;
        this.consentDate = consentDate;
        this.isConsented = isConsented;
    }

    // 도메인 모델에서 엔티티로 변환
    public static UserConsentEntity from(UserConsent userConsent) {
        return new UserConsentEntity(
                userConsent.getUserId(),
                userConsent.getTermsId(),
                userConsent.getConsentDate(),
                userConsent.isConsented()
        );
    }

    // 엔티티에서 도메인 모델로 변환
    public UserConsent toDomain() {
        return UserConsent.of(
                userId,
                termsId,
                consentDate,
                isConsented
        );
    }

    // 동의 상태 업데이트
    public void updateConsent(boolean isConsented, LocalDate consentDate) {
        this.isConsented = isConsented;
        this.consentDate = consentDate;
    }
}