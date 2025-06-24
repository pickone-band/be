package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomUserService {

  private final ChatRoomUserRepository chatRoomUserRepository;

  public boolean isUserInRoom(Long userId, Long roomId) {
    boolean exists = chatRoomUserRepository.existsByUserIdAndChatRoomId(userId, roomId);
    log.info("채팅방 참여 여부 확인: userId={}, roomId={}, result={}", userId, roomId, exists);
    return exists;
  }

  public ChatRoomUserEntity getParticipation(Long userId, Long roomId) {
    return chatRoomUserRepository.findByUserIdAndChatRoomId(userId, roomId)
        .orElseThrow(() -> {
          log.warn("참여 정보 없음: userId={}, roomId={}", userId, roomId);
          return new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        });
  }

  public long countParticipants(Long roomId) {
    long count = chatRoomUserRepository.countByChatRoomId(roomId);
    log.info("채팅방 참여자 수 조회: roomId={}, count={}", roomId, count);
    return count;
  }

  public List<ChatRoomUserEntity> getParticipants(Long roomId) {
    log.info("채팅방 참여자 목록 조회: roomId={}", roomId);
    return chatRoomUserRepository.findByChatRoomId(roomId);
  }
}
