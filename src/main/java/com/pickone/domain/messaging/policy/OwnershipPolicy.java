package com.pickone.domain.messaging.policy;

import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OwnershipPolicy {
  private final ChatRoomUserRepository chatRoomUserRepository;

  public void checkOwner(Long userId, Long roomId) {
    ChatRoomUserEntity entity = chatRoomUserRepository.findByUserIdAndChatRoomId(userId, roomId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED));
    if (!entity.isOwner()) {
      throw new BusinessException(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN);
    }
  }
}