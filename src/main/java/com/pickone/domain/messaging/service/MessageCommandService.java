package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.dto.SendMessageRequest;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.model.policy.MessageSendPolicy;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import com.pickone.domain.notification.event.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageCommandService {
  private final MessageMongoRepository messageRepository;
  private final MessageReadStatusMongoRepository readStatusRepository;
  private final MessageSendPolicy messageSendPolicy;
  private final ApplicationEventPublisher eventPublisher;

  public MessageDto sendMessage(Long senderId, SendMessageRequest request) {
    messageSendPolicy.validateCanSend(senderId, request.roomId());

    // recipientIds는 단톡이면 여러 명, 1:1이면 1명
    List<Long> recipientIds = messageSendPolicy.getRecipientIds(request.roomId(), senderId);

    // receiverId는 1:1일 때만 전달, 단톡이면 null
    Long receiverId = (recipientIds.size() == 1) ? recipientIds.get(0) : null;

    // 메시지 생성 (정적 팩토리 of 메서드 사용)
    MessageDocument message = MessageDocument.of(
        request.roomId(),
        senderId,
        receiverId,
        request.content(),
        LocalDateTime.now()
    );

    messageRepository.save(message);

    // 이벤트 발행 (수신자 전부 전달)
    eventPublisher.publishEvent(new MessageSentEvent(senderId, recipientIds, message.getContent()));

    return MessageDto.of(message);
  }
}
