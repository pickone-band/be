package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.ReadCountDto;
import com.pickone.domain.messaging.model.document.MessageReadStatusDocument;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageReadService {

  private final MessageReadStatusMongoRepository readRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;

  public void markAsRead(String messageId, Long userId) {
    log.info("메시지 읽음 처리 요청: messageId={}, userId={}", messageId, userId);
    readRepository.findByMessageIdAndUserId(messageId, userId).ifPresentOrElse(
        existing -> log.debug("이미 읽은 메시지: messageId={}, userId={}", messageId, userId),
        () -> {
          readRepository.save(new MessageReadStatusDocument(
              null, messageId, userId, LocalDateTime.now()
          ));
          log.info("읽음 처리 완료: messageId={}, userId={}", messageId, userId);
        }
    );
  }

  public long getReadCount(String messageId) {
    long count = readRepository.findByMessageId(messageId).size();
    log.info("메시지 읽은 인원 수: messageId={}, count={}", messageId, count);
    return count;
  }

  public long getTotalParticipantCount(Long roomId) {
    long total = chatRoomUserRepository.countByChatRoomId(roomId);
    log.info("채팅방 참여자 총 수: roomId={}, count={}", roomId, total);
    return total;
  }

  public ReadCountDto getReadStatus(String messageId, Long roomId) {
    long total = getTotalParticipantCount(roomId);
    long read = getReadCount(messageId);
    log.info("읽음 통계 조회: messageId={}, read={}, total={}", messageId, read, total);
    return new ReadCountDto(messageId, read, total);
  }
}
