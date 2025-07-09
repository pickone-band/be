package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.document.Message;
import com.pickone.domain.messaging.dto.MessageResponse;
import com.pickone.domain.messaging.mapper.MessageMapper;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageQueryServiceTest {

  @InjectMocks
  private MessageQueryService messageQueryService;

  @Mock
  private MessageAggregationRepository messageAggregationRepository;

  @Mock
  private MessageReadStatusMongoRepository readStatusRepository;

  @Mock
  private MessageMapper messageMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getMessages_success() {
    // given
    Long roomId = 1L;
    Message message1 = mock(Message.class);
    Message message2 = mock(Message.class);
    List<Message> messageList = List.of(message1, message2);

    MessageResponse response1 = new MessageResponse(roomId, 1L, List.of(2L), "Hi", LocalDateTime.now());
    MessageResponse response2 = new MessageResponse(roomId, 2L, List.of(1L), "Hello", LocalDateTime.now());
    List<MessageResponse> expectedResponses = List.of(response1, response2);

    when(messageAggregationRepository.findAllByRoomId(roomId)).thenReturn(messageList);
    when(messageMapper.toResponseList(messageList)).thenReturn(expectedResponses);

    // when
    List<MessageResponse> result = messageQueryService.getMessages(roomId);

    // then
    assertEquals(2, result.size());
    verify(messageAggregationRepository).findAllByRoomId(roomId);
    verify(messageMapper).toResponseList(messageList);
  }

  @Test
  void countUnread_success() {
    // given
    Long roomId = 1L;
    Long userId = 2L;
    long unreadCount = 3L;

    when(readStatusRepository.countByRoomIdAndUserIdAndIsReadFalse(roomId, userId)).thenReturn(unreadCount);

    // when
    long result = messageQueryService.countUnread(roomId, userId);

    // then
    assertEquals(unreadCount, result);
    verify(readStatusRepository).countByRoomIdAndUserIdAndIsReadFalse(roomId, userId);
  }
}
