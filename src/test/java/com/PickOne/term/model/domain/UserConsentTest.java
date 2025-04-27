package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class UserConsentTest {

    @Test
    @DisplayName("유효한 파라미터로 UserConsent를 생성할 수 있다")
    void createUserConsentWithValidParameters() {
        // given
        Long userId = 1L;
        Long termsId = 2L;
        LocalDate consentDate = LocalDate.now();
        boolean isConsented = true;

        // when
        UserConsent userConsent = UserConsent.of(userId, termsId, consentDate, isConsented);

        // then
        assertThat(userConsent).isNotNull();
        assertThat(userConsent.getUserId()).isEqualTo(userId);
        assertThat(userConsent.getTermsId()).isEqualTo(termsId);
        assertThat(userConsent.getConsentDate()).isEqualTo(consentDate);
        assertThat(userConsent.isConsented()).isEqualTo(isConsented);
    }

    @Test
    @DisplayName("필수 파라미터가 null일 경우 예외가 발생한다")
    void throwsExceptionWhenRequiredParametersAreNull() {
        // given
        Long userId = 1L;
        Long termsId = 2L;
        LocalDate consentDate = LocalDate.now();
        boolean isConsented = true;

        // then
        assertThatThrownBy(() -> UserConsent.of(null, termsId, consentDate, isConsented))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자 ID는 null일 수 없습니다");

        assertThatThrownBy(() -> UserConsent.of(userId, null, consentDate, isConsented))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("약관 ID는 null일 수 없습니다");

        assertThatThrownBy(() -> UserConsent.of(userId, termsId, null, isConsented))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("동의일은 null일 수 없습니다");
    }
}

