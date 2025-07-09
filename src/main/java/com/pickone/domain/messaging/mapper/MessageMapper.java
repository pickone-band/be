package com.pickone.domain.messaging.mapper;

import com.pickone.domain.messaging.document.Message;
import com.pickone.domain.messaging.dto.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MessageMapper {

  public MessageResponse toResponse(Message message) {
    if (message == null) return null;

    Set<Long> recipientIds = message.getReadUserIds();

    return new MessageResponse(
        message.getRoomId(),
        message.getSenderId(),
        List.copyOf(recipientIds),
        message.getContent(),
        message.getSentAt()
    );
  }

  public List<MessageResponse> toResponseList(List<Message> messages) {
    return messages.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }
}
