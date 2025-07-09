package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.document.Message;
import com.pickone.domain.messaging.dto.MessageResponse;
import com.pickone.domain.messaging.dto.SendMessageRequest;
import com.pickone.domain.messaging.mapper.MessageMapper;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.notification.event.MessageSentEvent;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageCommandServiceTest {

  @InjectMocks
  private MessageCommandService messageCommandService;

  @Mock
  private MessageMongoRepository messageRepository;

  @Mock
  private ChatRoomUserRepository chatRoomUserRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private MessageMapper messageMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void sendMessage_success_groupChat() {
    // given
    Long senderId = 1L;
    Long roomId = 10L;
    SendMessageRequest request = new SendMessageRequest(roomId, "Hello, Group!");
    List<Long> participantIds = List.of(1L, 2L, 3L);

    Message message = mock(Message.class);
    MessageResponse response = new MessageResponse(roomId, senderId, List.of(2L, 3L), request.content(), message.getSentAt());

    when(chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)).thenReturn(true);
    when(chatRoomUserRepository.findUserIdsByRoomId(roomId)).thenReturn(participantIds);
    when(messageMapper.toResponse(any())).thenReturn(response);

    // when
    MessageResponse result = messageCommandService.sendMessage(senderId, request);

    // then
    assertNotNull(result);
    assertEquals(request.content(), result.content());
    assertEquals(2, result.recipientIds().size());

    verify(messageRepository).save(any(Message.class));
    verify(eventPublisher).publishEvent(any(MessageSentEvent.class));
  }

  @Test
  void sendMessage_success_directMessage() {
    // given
    Long senderId = 1L;
    Long receiverId = 2L;
    Long roomId = 10L;
    SendMessageRequest request = new SendMessageRequest(roomId, "Hello!");

    List<Long> participantIds = List.of(senderId, receiverId);
    Message message = mock(Message.class);
    MessageResponse response = new MessageResponse(roomId, senderId, List.of(receiverId), request.content(), message.getSentAt());

    when(chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)).thenReturn(true);
    when(chatRoomUserRepository.findUserIdsByRoomId(roomId)).thenReturn(participantIds);
    when(messageMapper.toResponse(any())).thenReturn(response);

    // when
    MessageResponse result = messageCommandService.sendMessage(senderId, request);

    // then
    assertNotNull(result);
    assertEquals(request.content(), result.content());
    assertEquals(1, result.recipientIds().size());
    assertEquals(receiverId, result.recipientIds().get(0));

    verify(messageRepository).save(any(Message.class));
    verify(eventPublisher).publishEvent(any(MessageSentEvent.class));
  }

  @Test
  void sendMessage_fail_notParticipant() {
    // given
    Long senderId = 1L;
    Long roomId = 10L;
    SendMessageRequest request = new SendMessageRequest(roomId, "Unauthorized message");

    when(chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)).thenReturn(false);

    // when & then
    BusinessException ex = assertThrows(BusinessException.class, () -> messageCommandService.sendMessage(senderId, request));
    assertEquals(ErrorCode.CHAT_ROOM_ACCESS_DENIED, ex.getErrorCode());

    verify(messageRepository, never()).save(any(Message.class));
    verify(eventPublisher, never()).publishEvent(any());
  }

  @Test
  void sendMessage_recipientListExcludesSender() {
    // given
    Long senderId = 1L;
    Long roomId = 10L;
    SendMessageRequest request = new SendMessageRequest(roomId, "Check recipient IDs");

    List<Long> participantIds = List.of(1L, 2L, 3L, 4L);
    when(chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)).thenReturn(true);
    when(chatRoomUserRepository.findUserIdsByRoomId(roomId)).thenReturn(participantIds);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    when(messageMapper.toResponse(any())).thenAnswer(invocation -> {
      Message msg = invocation.getArgument(0);
      return new MessageResponse(msg.getRoomId(), msg.getSenderId(), participantIds.stream().filter(id -> !id.equals(senderId)).toList(), msg.getContent(), msg.getSentAt());
    });

    // when
    MessageResponse result = messageCommandService.sendMessage(senderId, request);

    // then
    verify(messageRepository).save(messageCaptor.capture());
    Message savedMessage = messageCaptor.getValue();

    assertNull(savedMessage.getReceiverId()); // group chat
    assertEquals(3, result.recipientIds().size());
    assertFalse(result.recipientIds().contains(senderId));
  }
}
