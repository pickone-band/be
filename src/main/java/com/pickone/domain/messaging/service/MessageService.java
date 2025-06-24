package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.notification.model.domain.MessageSentEvent;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

  private final MessageMongoRepository messageMongoRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;
  private final ApplicationEventPublisher eventPublisher;

  public MessageDocument sendMessage(Long roomId, Long senderId, String content) {
    log.info("메시지 전송 요청: roomId={}, senderId={}, content={}", roomId, senderId, content);

    if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)) {
      log.warn("메시지 전송 실패 - 참여자 아님: senderId={}, roomId={}", senderId, roomId);
      throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }

    MessageDocument message = messageMongoRepository.save(
        new MessageDocument(null, roomId, senderId, content, LocalDateTime.now())
    );
    log.info("메시지 저장 완료: messageId={}", message.getId());

    List<Long> recipientIds = chatRoomUserRepository.findByChatRoomId(roomId).stream()
        .map(chatRoomUser -> chatRoomUser.getUser().getId())
        .filter(id -> !id.equals(senderId))
        .toList();

    eventPublisher.publishEvent(new MessageSentEvent(senderId, recipientIds, content));
    log.info("메시지 전송 이벤트 발행: senderId={}, recipients={}", senderId, recipientIds);

    return message;
  }

  public List<MessageDocument> getMessagesByRoom(Long roomId) {
    log.info("채팅 메시지 목록 조회: roomId={}", roomId);
    return messageMongoRepository.findByRoomIdOrderBySentAtAsc(roomId);
  }

  public MessageDocument getLastMessage(Long roomId) {
    log.info("마지막 메시지 조회: roomId={}", roomId);
    return messageMongoRepository.findTopByRoomIdOrderBySentAtDesc(roomId);
  }
}
