package com.pickone.domain.messaging.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageQueryServiceTest {

  @InjectMocks
  private MessageQueryService service;

  @Mock
  private MessageReadStatusMongoRepository readRepo;

  @Mock
  private MessageAggregationRepository aggRepo;

  @Test
  void countUnread_returnsCorrect() {
    given(readRepo.countByRoomIdAndUserIdAndIsReadFalse(1L, 2L)).willReturn(4L);
    long count = service.countUnread(1L, 2L);
    assertEquals(4L, count);
  }

  @Test
  void getMessages_returnsList() {
    MessageDocument doc = new MessageDocument(null, 1L, 2L, "hi", LocalDateTime.now());
    given(aggRepo.findAllByRoomId(1L)).willReturn(List.of(doc));

    List<MessageDto> result = service.getMessages(1L);
    assertEquals(1, result.size());
    assertEquals("hi", result.get(0).content());
  }
}
