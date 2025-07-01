package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageQueryServiceTest {

  @Mock private MessageAggregationRepository messageAggregationRepository;
  @Mock private MessageReadStatusMongoRepository readStatusRepository;

  @InjectMocks private MessageQueryService sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("getMessages: 메시지 리스트를 DTO로 반환")
  void getMessages_success() {
    Long roomId = 10L;

    MessageDocument doc1 = mock(MessageDocument.class);
    MessageDocument doc2 = mock(MessageDocument.class);
    List<MessageDocument> docs = Arrays.asList(doc1, doc2);

    when(messageAggregationRepository.findAllByRoomId(roomId)).thenReturn(docs);

    MessageDto dto1 = mock(MessageDto.class);
    MessageDto dto2 = mock(MessageDto.class);

    try (MockedStatic<MessageDto> msgDtoStatic = mockStatic(MessageDto.class)) {
      msgDtoStatic.when(() -> MessageDto.of(doc1)).thenReturn(dto1);
      msgDtoStatic.when(() -> MessageDto.of(doc2)).thenReturn(dto2);

      List<MessageDto> result = sut.getMessages(roomId);

      assertThat(result).containsExactly(dto1, dto2);
      verify(messageAggregationRepository).findAllByRoomId(roomId);
    }
  }

  @Test
  @DisplayName("getMessages: 메시지가 없으면 빈 리스트 반환")
  void getMessages_empty() {
    Long roomId = 11L;
    when(messageAggregationRepository.findAllByRoomId(roomId)).thenReturn(Collections.emptyList());

    List<MessageDto> result = sut.getMessages(roomId);

    assertThat(result).isEmpty();
    verify(messageAggregationRepository).findAllByRoomId(roomId);
  }

  @Test
  @DisplayName("countUnread: 읽지 않은 메시지 수 반환")
  void countUnread_success() {
    Long roomId = 12L, userId = 22L;
    long count = 7L;

    when(readStatusRepository.countByRoomIdAndUserIdAndIsReadFalse(roomId, userId)).thenReturn(count);

    long result = sut.countUnread(roomId, userId);

    assertThat(result).isEqualTo(count);
    verify(readStatusRepository).countByRoomIdAndUserIdAndIsReadFalse(roomId, userId);
  }
}
