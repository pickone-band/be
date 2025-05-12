package com.PickOne.domain.messaging.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class MessageContentTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("메시지 내용은 비어있을 수 없다")
    void contentCannotBeEmpty(String invalidContent) {
        assertThatThrownBy(() -> new MessageContent(invalidContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메시지 내용은 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("메시지 내용은 2000자를 초과할 수 없다")
    void contentCannotExceed2000Characters() {
        // Given
        String longContent = "a".repeat(2001);

        // When, Then
        assertThatThrownBy(() -> new MessageContent(longContent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("메시지 내용은 2000자를 초과할 수 없습니다");
    }

    @Test
    @DisplayName("유효한 메시지 내용으로 객체를 생성할 수 있다")
    void createValidMessageContent() {
        // Given
        String content = "유효한 메시지 내용입니다";

        // When
        MessageContent messageContent = new MessageContent(content);

        // Then
        assertThat(messageContent.getValue()).isEqualTo(content);
    }
}