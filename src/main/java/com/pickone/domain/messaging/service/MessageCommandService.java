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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageCommandService {

  private final MessageMongoRepository messageRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final MessageMapper messageMapper;

  public MessageResponse sendMessage(Long senderId, SendMessageRequest request) {
    // 채팅방 참가 여부 검증
    if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, request.roomId())) {
      throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }

    List<Long> participantIds = chatRoomUserRepository.findUserIdsByRoomId(request.roomId());
    List<Long> recipientIds = participantIds.stream()
        .filter(id -> !id.equals(senderId))
        .toList();

    Long receiverId = recipientIds.size() == 1 ? recipientIds.get(0) : null;

    Message message = Message.create(
        request.roomId(),
        senderId,
        receiverId,
        request.content(),
        LocalDateTime.now()
    );

    messageRepository.save(message);

    log.info("[MessageCommandService] 메시지 전송 완료 - senderId: {}, roomId: {}, content: {}",
        senderId, request.roomId(), request.content());

    eventPublisher.publishEvent(new MessageSentEvent(senderId, recipientIds, message.getContent()));
    return messageMapper.toResponse(message);
  }
}