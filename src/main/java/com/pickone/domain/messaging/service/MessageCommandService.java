package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.dto.SendMessageRequest;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.policy.MessageSendPolicy;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import com.pickone.domain.notification.model.domain.MessageSentEvent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageCommandService {
  private final MessageMongoRepository messageRepository;
  private final MessageReadStatusMongoRepository readStatusRepository;
  private final MessageSendPolicy messageSendPolicy;
  private final ApplicationEventPublisher eventPublisher;

  public MessageDto sendMessage(Long senderId, SendMessageRequest request) {
    messageSendPolicy.validateCanSend(senderId, request.roomId());
    MessageDocument message = new MessageDocument(
        request.roomId(), senderId, request.content(), LocalDateTime.now()
    );
    messageRepository.save(message);
    List<Long> recipientIds = messageSendPolicy.getRecipientIds(request.roomId(), senderId);
    eventPublisher.publishEvent(new MessageSentEvent(senderId, recipientIds, message.getContent()));
    return MessageDto.of(message);
  }
}