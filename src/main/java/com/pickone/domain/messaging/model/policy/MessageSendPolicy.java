package com.pickone.domain.messaging.model.policy;

import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessageSendPolicy {

  private final ChatRoomUserRepository chatRoomUserRepository;

  public void validateCanSend(Long senderId, Long roomId) {
    if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)) {
      throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }
  }

  public List<Long> getRecipientIds(Long roomId, Long senderId) {
    List<Long> participantIds = chatRoomUserRepository.findUserIdsByRoomId(roomId);

    if (participantIds == null || participantIds.isEmpty()) {
      return List.of();
    }

    return participantIds.stream()
        .filter(id -> !id.equals(senderId))  // 보낸 사람 제외
        .toList();
  }

}