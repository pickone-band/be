package com.PickOne.domain.messaging.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientIdTest {

    @Test
    @DisplayName("수신자 ID는 null일 수 없다")
    void recipientIdCannotBeNull() {
        assertThatThrownBy(() -> new RecipientId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수신자 ID는 양수여야 합니다");
    }

    @Test
    @DisplayName("수신자 ID는 0이나 음수일 수 없다")
    void recipientIdCannotBeZeroOrNegative() {
        assertThatThrownBy(() -> new RecipientId(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수신자 ID는 양수여야 합니다");

        assertThatThrownBy(() -> new RecipientId(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수신자 ID는 양수여야 합니다");
    }

    @Test
    @DisplayName("유효한 수신자 ID로 객체를 생성할 수 있다")
    void createValidRecipientId() {
        // Given
        Long id = 1L;

        // When
        RecipientId recipientId = new RecipientId(id);

        // Then
        assertThat(recipientId.getValue()).isEqualTo(id);
    }
}