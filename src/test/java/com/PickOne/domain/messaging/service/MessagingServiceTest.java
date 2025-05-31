package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessagingServiceTest {

    private MessageRepository messageRepository;
    private MessagingService messagingService;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        messagingService = new MessagingServiceImpl(messageRepository);
    }

    @Test
    @DisplayName("메시지 전송 - 성공")
    void sendMessage_success() {
        Message created = Message.create(1L, 2L, "Hello");
        when(messageRepository.save(any())).thenReturn(created);

        Message result = messagingService.sendMessage(1L, 2L, "Hello");

        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getRecipientId()).isEqualTo(2L);
        assertThat(result.getContent()).isEqualTo("Hello");
    }

    @Test
    @DisplayName("메시지 읽음 표시 - 성공")
    void markAsRead_success() {
        Message original = new Message("id123", 1L, 2L, "Test", MessageStatus.DELIVERED, LocalDateTime.now(), LocalDateTime.now(), null);
        Message updated = original.markAsRead();

        when(messageRepository.findById("id123")).thenReturn(Optional.of(original));
        when(messageRepository.save(any())).thenReturn(updated);

        Message result = messagingService.markAsRead("id123");

        assertThat(result.getStatus()).isEqualTo(MessageStatus.READ);
        assertThat(result.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("읽지 않은 메시지 필터링")
    void getUnreadMessages_success() {
        List<Message> all = List.of(
                new Message("1", 1L, 2L, "Hi", MessageStatus.SENT, LocalDateTime.now(), null, null),
                new Message("2", 1L, 2L, "Hey", MessageStatus.READ, LocalDateTime.now(), null, LocalDateTime.now())
        );

        when(messageRepository.findByRecipientId(2L)).thenReturn(all);

        List<Message> unread = messagingService.getUnreadMessages(2L);
        assertThat(unread).hasSize(1);
        assertThat(unread.get(0).getStatus()).isNotEqualTo(MessageStatus.READ);
    }

    @Test
    @DisplayName("메시지 삭제 - 성공")
    void deleteMessage_success() {
        doNothing().when(messageRepository).delete("id123");
        messagingService.deleteMessage("id123");
        verify(messageRepository).delete("id123");
    }
}
