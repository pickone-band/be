package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ContentTest {

    @Test
    @DisplayName("유효한 내용으로 Content를 생성할 수 있다")
    void createContentWithValidValue() {
        // given
        String validContent = "본 약관은 서비스 이용에 관한 내용입니다.";

        // when
        Content content = Content.of(validContent);

        // then
        assertThat(content).isNotNull();
        assertThat(content.getValue()).isEqualTo(validContent);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("빈 문자열이나 공백으로 Content를 생성할 수 없다")
    void throwsExceptionWhenContentIsEmptyOrBlank(String invalidContent) {
        // then
        assertThatThrownBy(() -> Content.of(invalidContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("내용은 비어있을 수 없습니다");
    }
}