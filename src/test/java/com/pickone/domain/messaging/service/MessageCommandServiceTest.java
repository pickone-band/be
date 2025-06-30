package com.pickone.domain.messaging.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.dto.SendMessageRequest;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.policy.MessageSendPolicy;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class MessageCommandServiceTest {

  @InjectMocks
  private MessageCommandService messageCommandService;

  @Mock
  private MessageMongoRepository messageRepository;

  @Mock
  private MessageReadStatusMongoRepository readStatusRepository;

  @Mock
  private MessageSendPolicy messageSendPolicy;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Test
  void sendMessage_정상동작() {
    // given
    Long senderId = 1L;
    SendMessageRequest request = new SendMessageRequest(100L, "hello");

    // policy 검증이 예외 없이 통과되도록 설정
    // (void method는 doNothing().when(...) 사용)
    // 또는 생략 가능 (예외 안 던지는 한은)

    MessageDocument saved = new MessageDocument(
        "m1", request.roomId(), senderId, request.content(), LocalDateTime.now()
    );

    given(messageRepository.save(any(MessageDocument.class))).willReturn(saved);

    // when
    MessageDto result = messageCommandService.sendMessage(senderId, request);

    // then
    assertNotNull(result);
    assertEquals(request.roomId(), result.roomId());
    assertEquals(senderId, result.senderId());
    assertEquals(request.content(), result.content());
  }
}
