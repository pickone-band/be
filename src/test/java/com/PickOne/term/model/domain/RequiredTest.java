package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequiredTest {

    @Test
    @DisplayName("필수 여부를 설정할 수 있다")
    void createRequiredWithValue() {
        // when
        Required required = Required.of(true);
        Required optional = Required.of(false);

        // then
        assertThat(required.isValue()).isTrue();
        assertThat(optional.isValue()).isFalse();
    }
}