package com.PickOne.domain.messaging.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTest {

    @Test
    @DisplayName("메시지를 생성할 수 있다")
    void createMessage() {
        // Given
        Long senderId = 1L;
        Long recipientId = 2L;
        String content = "테스트 메시지입니다";

        // When
        Message message = Message.create(senderId, recipientId, content);

        // Then
        assertThat(message).isNotNull();
        assertThat(message.getId()).isNotNull();
        assertThat(message.getSenderIdValue()).isEqualTo(senderId);
        assertThat(message.getRecipientIdValue()).isEqualTo(recipientId);
        assertThat(message.getContentValue()).isEqualTo(content);
        assertThat(message.getStatus()).isEqualTo(MessageStatus.SENT);
        assertThat(message.getSentAt()).isNotNull();
        assertThat(message.getDeliveredAt()).isNull();
        assertThat(message.getReadAt()).isNull();
    }

    @Test
    @DisplayName("메시지를 배달됨으로 표시할 수 있다")
    void markMessageDelivered() {
        // Given
        Message message = Message.create(1L, 2L, "테스트 메시지입니다");

        // When
        Message deliveredMessage = message.markDelivered();

        // Then
        assertThat(deliveredMessage.getStatus()).isEqualTo(MessageStatus.DELIVERED);
        assertThat(deliveredMessage.getDeliveredAt()).isNotNull();
        assertThat(deliveredMessage.getReadAt()).isNull();
    }

    @Test
    @DisplayName("메시지를 읽음으로 표시할 수 있다")
    void markMessageRead() {
        // Given
        Message message = Message.create(1L, 2L, "테스트 메시지입니다");

        // When
        Message readMessage = message.markRead();

        // Then
        assertThat(readMessage.getStatus()).isEqualTo(MessageStatus.READ);
        assertThat(readMessage.getDeliveredAt()).isNotNull(); // 배달 시간도 자동으로 설정됨
        assertThat(readMessage.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 배달됨 상태인 메시지를 읽음으로 표시할 수 있다")
    void markDeliveredMessageAsRead() {
        // Given
        Message message = Message.create(1L, 2L, "테스트 메시지입니다");
        Message deliveredMessage = message.markDelivered();
        LocalDateTime deliveredAt = deliveredMessage.getDeliveredAt();

        // When
        Message readMessage = deliveredMessage.markRead();

        // Then
        assertThat(readMessage.getStatus()).isEqualTo(MessageStatus.READ);
        assertThat(readMessage.getDeliveredAt()).isEqualTo(deliveredAt); // 기존 배달 시간이 유지됨
        assertThat(readMessage.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 읽음 상태인 메시지를 다시 읽음으로 표시해도 상태가 변하지 않는다")
    void markReadMessageAsReadAgain() {
        // Given
        Message message = Message.create(1L, 2L, "테스트 메시지입니다");
        Message readMessage = message.markRead();
        LocalDateTime readAt = readMessage.getReadAt();

        // When
        Message readAgainMessage = readMessage.markRead();

        // Then
        assertThat(readAgainMessage).isEqualTo(readMessage);
        assertThat(readAgainMessage.getReadAt()).isEqualTo(readAt);
    }
}