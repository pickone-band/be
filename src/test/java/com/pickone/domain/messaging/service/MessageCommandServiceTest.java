package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.dto.SendMessageRequest;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.model.policy.MessageSendPolicy;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import com.pickone.domain.notification.event.MessageSentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageCommandServiceTest {

  @Mock private MessageMongoRepository messageRepository;
  @Mock private MessageReadStatusMongoRepository readStatusRepository;
  @Mock private MessageSendPolicy messageSendPolicy;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private MessageCommandService sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("정상적으로 메시지 전송 (단톡방)")
  void sendMessage_success() {
    Long senderId = 1L, roomId = 100L;
    String content = "hello world";
    SendMessageRequest req = new SendMessageRequest(roomId, content);

    // 정책: 보낼 수 있는지 검증
    doNothing().when(messageSendPolicy).validateCanSend(senderId, roomId);

    // 정책: 수신자 목록 (단톡방, 여러 명)
    List<Long> recipientIds = List.of(2L, 3L);
    when(messageSendPolicy.getRecipientIds(roomId, senderId)).thenReturn(recipientIds);

    MessageDocument message = mock(MessageDocument.class);

    // Argument matcher 일관 적용: eq/any
    try (MockedStatic<MessageDocument> msgStatic = mockStatic(MessageDocument.class)) {
      msgStatic.when(() -> MessageDocument.of(
          eq(roomId), eq(senderId), eq(null), eq(content), any(LocalDateTime.class)
      )).thenReturn(message);

      when(message.getContent()).thenReturn(content);

      when(messageRepository.save(message)).thenReturn(message);

      MessageDto dto = mock(MessageDto.class);
      try (MockedStatic<MessageDto> dtoStatic = mockStatic(MessageDto.class)) {
        dtoStatic.when(() -> MessageDto.of(message)).thenReturn(dto);

        // when
        MessageDto result = sut.sendMessage(senderId, req);

        // then
        assertThat(result).isSameAs(dto);

        verify(messageSendPolicy).validateCanSend(senderId, roomId);
        verify(messageSendPolicy).getRecipientIds(roomId, senderId);
        verify(messageRepository).save(message);
        verify(eventPublisher).publishEvent(any(MessageSentEvent.class));
      }
    }
  }

  @Test
  @DisplayName("1:1 메시지 전송 시 receiverId가 설정됨")
  void sendMessage_directMessage_receiverIdSet() {
    Long senderId = 1L, roomId = 100L;
    String content = "hi";
    SendMessageRequest req = new SendMessageRequest(roomId, content);

    doNothing().when(messageSendPolicy).validateCanSend(senderId, roomId);

    // 수신자가 1명인 경우 (1:1)
    List<Long> recipientIds = List.of(2L);
    when(messageSendPolicy.getRecipientIds(roomId, senderId)).thenReturn(recipientIds);

    MessageDocument message = mock(MessageDocument.class);

    try (MockedStatic<MessageDocument> msgStatic = mockStatic(MessageDocument.class)) {
      msgStatic.when(() -> MessageDocument.of(
          eq(roomId), eq(senderId), eq(2L), eq(content), any(LocalDateTime.class)
      )).thenReturn(message);

      when(message.getContent()).thenReturn(content);

      when(messageRepository.save(message)).thenReturn(message);

      MessageDto dto = mock(MessageDto.class);
      try (MockedStatic<MessageDto> dtoStatic = mockStatic(MessageDto.class)) {
        dtoStatic.when(() -> MessageDto.of(message)).thenReturn(dto);

        MessageDto result = sut.sendMessage(senderId, req);

        assertThat(result).isSameAs(dto);

        verify(messageSendPolicy).validateCanSend(senderId, roomId);
        verify(messageSendPolicy).getRecipientIds(roomId, senderId);
        verify(messageRepository).save(message);
        verify(eventPublisher).publishEvent(any(MessageSentEvent.class));
      }
    }
  }

  @Test
  @DisplayName("정책 위반 시 예외 발생")
  void sendMessage_policyViolation() {
    Long senderId = 1L, roomId = 100L;
    String content = "forbidden";
    SendMessageRequest req = new SendMessageRequest(roomId, content);

    doThrow(new IllegalStateException("Not allowed"))
        .when(messageSendPolicy).validateCanSend(senderId, roomId);

    assertThatThrownBy(() -> sut.sendMessage(senderId, req))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Not allowed");

    verify(messageSendPolicy).validateCanSend(senderId, roomId);
    verify(messageSendPolicy, never()).getRecipientIds(anyLong(), anyLong());
    verifyNoInteractions(messageRepository, eventPublisher);
  }
}
