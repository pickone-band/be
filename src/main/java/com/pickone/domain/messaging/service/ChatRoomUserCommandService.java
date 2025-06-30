package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.model.entity.ChatRole;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.policy.OwnershipPolicy;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomUserCommandService {
  private final ChatRoomUserRepository chatRoomUserRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserJpaRepository userJpaRepository;
  private final OwnershipPolicy ownershipPolicy;

  public void inviteUser(Long roomId, Long inviterId, Long targetUserId) {
    ownershipPolicy.checkOwner(inviterId, roomId);

    if (chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)) {
      return;
    }

    ChatRoomEntity room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

    UserEntity target = userJpaRepository.findById(targetUserId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    chatRoomUserRepository.save(
        new ChatRoomUserEntity(room, target, ChatRole.MEMBER));
  }

  public void deleteRoom(Long roomId, Long requesterId) {
    ownershipPolicy.checkOwner(requesterId, roomId);
    chatRoomUserRepository.deleteAllByChatRoomId(roomId);
  }
}