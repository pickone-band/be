package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TermsTypeTest {

    @Test
    @DisplayName("TermsType 열거형의 기본 값이 올바르게 설정되었는지 확인한다")
    void checkTermsTypeDefaultValues() {
        // then
        assertThat(TermsType.SERVICE.getValue()).isEqualTo("service");
        assertThat(TermsType.PRIVACY.getValue()).isEqualTo("privacy");
        assertThat(TermsType.MARKETING.getValue()).isEqualTo("marketing");

        assertThat(TermsType.SERVICE.getDisplayName()).isEqualTo("서비스 이용약관");
        assertThat(TermsType.PRIVACY.getDisplayName()).isEqualTo("개인정보 처리방침");
        assertThat(TermsType.MARKETING.getDisplayName()).isEqualTo("마케팅 정보 수신 동의");
    }

    @ParameterizedTest
    @CsvSource({
            "service, SERVICE",
            "privacy, PRIVACY",
            "marketing, MARKETING"
    })
    @DisplayName("문자열로부터 TermsType을 올바르게 찾을 수 있다")
    void findTermsTypeFromString(String value, TermsType expected) {
        // when
        TermsType actual = TermsType.fromString(value);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "SERVICE, service",
            "PRIVACY, privacy",
            "MARKETING, marketing"
    })
    @DisplayName("대소문자를 구분하지 않고 TermsType을 찾을 수 있다")
    void findTermsTypeIgnoringCase(String uppercase, String expected) {
        // when
        TermsType type = TermsType.fromString(uppercase);

        // then
        assertThat(type.getValue()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "unknown", "other"})
    @DisplayName("유효하지 않은 문자열로 TermsType을 찾을 수 없다")
    void throwsExceptionForInvalidString(String invalid) {
        // then
        assertThatThrownBy(() -> TermsType.fromString(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 약관 유형입니다");
    }

    @Test
    @DisplayName("필수 동의 약관 여부를 확인할 수 있다")
    void checkIfTermsTypeIsRequiredConsent() {
        // then
        assertThat(TermsType.SERVICE.isRequiredConsent()).isTrue();
        assertThat(TermsType.PRIVACY.isRequiredConsent()).isTrue();
        assertThat(TermsType.MARKETING.isRequiredConsent()).isFalse();
    }

    @Test
    @DisplayName("마케팅 약관 여부를 확인할 수 있다")
    void checkIfTermsTypeIsMarketingConsent() {
        // then
        assertThat(TermsType.SERVICE.isMarketingConsent()).isFalse();
        assertThat(TermsType.PRIVACY.isMarketingConsent()).isFalse();
        assertThat(TermsType.MARKETING.isMarketingConsent()).isTrue();
    }
}