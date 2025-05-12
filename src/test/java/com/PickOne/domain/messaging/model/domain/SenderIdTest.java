package com.PickOne.domain.messaging.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SenderIdTest {

    @Test
    @DisplayName("발신자 ID는 null일 수 없다")
    void senderIdCannotBeNull() {
        assertThatThrownBy(() -> new SenderId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("발신자 ID는 양수여야 합니다");
    }

    @Test
    @DisplayName("발신자 ID는 0이나 음수일 수 없다")
    void senderIdCannotBeZeroOrNegative() {
        assertThatThrownBy(() -> new SenderId(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("발신자 ID는 양수여야 합니다");

        assertThatThrownBy(() -> new SenderId(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("발신자 ID는 양수여야 합니다");
    }

    @Test
    @DisplayName("유효한 발신자 ID로 객체를 생성할 수 있다")
    void createValidSenderId() {
        // Given
        Long id = 1L;

        // When
        SenderId senderId = new SenderId(id);

        // Then
        assertThat(senderId.getValue()).isEqualTo(id);
    }
}