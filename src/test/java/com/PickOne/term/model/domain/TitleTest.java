package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class TitleTest {

    @Test
    @DisplayName("유효한 제목으로 Title을 생성할 수 있다")
    void createTitleWithValidValue() {
        // given
        String validTitle = "서비스 이용약관";

        // when
        Title title = Title.of(validTitle);

        // then
        assertThat(title).isNotNull();
        assertThat(title.getValue()).isEqualTo(validTitle);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("빈 문자열이나 공백으로 Title을 생성할 수 없다")
    void throwsExceptionWhenTitleIsEmptyOrBlank(String invalidTitle) {
        // then
        assertThatThrownBy(() -> Title.of(invalidTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("제목은 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("최대 길이를 초과하는 제목으로 Title을 생성할 수 없다")
    void throwsExceptionWhenTitleExceedsMaxLength() {
        // given
        String tooLongTitle = "a".repeat(101);

        // then
        assertThatThrownBy(() -> Title.of(tooLongTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("제목은 100자를 초과할 수 없습니다");
    }
}

