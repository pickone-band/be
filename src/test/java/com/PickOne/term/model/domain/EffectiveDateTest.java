package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class EffectiveDateTest {

    @Test
    @DisplayName("유효한 날짜로 EffectiveDate를 생성할 수 있다")
    void createEffectiveDateWithValidDate() {
        // given
        LocalDate validDate = LocalDate.now();

        // when
        EffectiveDate effectiveDate = EffectiveDate.of(validDate);

        // then
        assertThat(effectiveDate).isNotNull();
        assertThat(effectiveDate.getValue()).isEqualTo(validDate);
    }

    @Test
    @DisplayName("null 날짜로 EffectiveDate를 생성할 수 없다")
    void throwsExceptionWhenDateIsNull() {
        // then
        assertThatThrownBy(() -> EffectiveDate.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("시행일은 null일 수 없습니다");
    }

    @Test
    @DisplayName("특정 날짜에 약관이 유효한지 확인할 수 있다")
    void checkIfTermsIsEffectiveAtGivenDate() {
        // given
        LocalDate effectiveDate = LocalDate.of(2023, 1, 1);
        EffectiveDate date = EffectiveDate.of(effectiveDate);

        LocalDate before = LocalDate.of(2022, 12, 31);
        LocalDate same = LocalDate.of(2023, 1, 1);
        LocalDate after = LocalDate.of(2023, 1, 2);

        // then
        assertThat(date.isEffectiveAt(before)).isFalse();
        assertThat(date.isEffectiveAt(same)).isTrue();
        assertThat(date.isEffectiveAt(after)).isTrue();
    }

}