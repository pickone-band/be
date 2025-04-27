package com.PickOne.term.model.entity;

import com.PickOne.term.model.domain.UserConsent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserConsentEntityTest {

    @Test
    @DisplayName("UserConsent 도메인 모델로부터 UserConsentEntity 생성")
    void from_ShouldCreateEntityFromDomain() {
        // given
        Long userId = 1L;
        Long termsId = 2L;
        LocalDate consentDate = LocalDate.of(2025, 4, 18);
        boolean isConsented = true;
        UserConsent userConsent = UserConsent.of(userId, termsId, consentDate, isConsented);

        // when
        UserConsentEntity entity = UserConsentEntity.from(userConsent);

        // then
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getTermsId()).isEqualTo(termsId);
        assertThat(entity.getConsentDate()).isEqualTo(consentDate);
        assertThat(entity.isConsented()).isEqualTo(isConsented);
    }

    @Test
    @DisplayName("UserConsentEntity로부터 UserConsent 도메인 모델 생성")
    void toDomain_ShouldCreateDomainFromEntity() {
        // given
        Long userId = 1L;
        Long termsId = 2L;
        LocalDate consentDate = LocalDate.of(2025, 4, 18);
        boolean isConsented = true;
        // UserConsentEntity의 생성자가 private이므로 from 메서드를 사용
        UserConsent userConsent = UserConsent.of(userId, termsId, consentDate, isConsented);
        UserConsentEntity entity = UserConsentEntity.from(userConsent);

        // when
        UserConsent domain = entity.toDomain();

        // then
        assertThat(domain.getUserId()).isEqualTo(userId);
        assertThat(domain.getTermsId()).isEqualTo(termsId);
        assertThat(domain.getConsentDate()).isEqualTo(consentDate);
        assertThat(domain.isConsented()).isEqualTo(isConsented);
    }

    @Test
    @DisplayName("동의 상태 업데이트")
    void updateConsent_ShouldUpdateConsentStatus() {
        // given
        Long userId = 1L;
        Long termsId = 2L;
        LocalDate initialConsentDate = LocalDate.of(2025, 4, 18);
        boolean initialConsent = true;
        // UserConsentEntity의 생성자가 private인지 확인하고, from 메서드를 사용해야 할 수도 있음
        // 현재 코드에서는 생성자를 직접 호출할 수 있다고 가정
        UserConsent userConsent = UserConsent.of(userId, termsId, initialConsentDate, initialConsent);
        UserConsentEntity entity = UserConsentEntity.from(userConsent);

        // when
        LocalDate newConsentDate = LocalDate.of(2025, 4, 19);
        boolean newConsent = false;
        entity.updateConsent(newConsent, newConsentDate);

        // then
        assertThat(entity.isConsented()).isEqualTo(newConsent);
        assertThat(entity.getConsentDate()).isEqualTo(newConsentDate);
    }

    @Test
    @DisplayName("UserConsent 도메인과 엔티티 간 변환 일관성 검증")
    void conversionConsistency_BetweenDomainAndEntity() {
        // given
        Long userId = 1L;
        Long termsId = 2L;
        LocalDate consentDate = LocalDate.of(2025, 4, 18);
        boolean isConsented = true;
        UserConsent originalDomain = UserConsent.of(userId, termsId, consentDate, isConsented);

        // when
        UserConsentEntity entity = UserConsentEntity.from(originalDomain);
        UserConsent convertedDomain = entity.toDomain();

        // then
        assertThat(convertedDomain.getUserId()).isEqualTo(originalDomain.getUserId());
        assertThat(convertedDomain.getTermsId()).isEqualTo(originalDomain.getTermsId());
        assertThat(convertedDomain.getConsentDate()).isEqualTo(originalDomain.getConsentDate());
        assertThat(convertedDomain.isConsented()).isEqualTo(originalDomain.isConsented());
    }
}