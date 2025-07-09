package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.entity.ChatRole;
import com.pickone.domain.messaging.entity.ChatRoom;
import com.pickone.domain.messaging.entity.ChatRoomUser;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomUserCommandService {

  private final ChatRoomUserRepository chatRoomUserRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserJpaRepository userJpaRepository;

  public void inviteUser(Long roomId, Long inviterId, Long targetUserId) {
    ChatRoom room = chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

    ChatRoomUser inviter = chatRoomUserRepository.findByUserIdAndChatRoomId(inviterId, roomId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED));

    if (!inviter.isOwner()) {
      throw new BusinessException(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN);
    }

    if (chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)) {
      return;
    }

    User target = userJpaRepository.findById(targetUserId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    chatRoomUserRepository.save(ChatRoomUser.create(room, target, ChatRole.MEMBER));

    log.info("[ChatRoomUserCommandService] 채팅방 초대 완료 - inviterId: {}, targetUserId: {}, roomId: {}", inviterId, targetUserId, roomId);
  }

  public void deleteRoom(Long roomId, Long requesterId) {
    ChatRoomUser requester = chatRoomUserRepository.findByUserIdAndChatRoomId(requesterId, roomId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED));

    if (!requester.isOwner()) {
      throw new BusinessException(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN);
    }

    chatRoomUserRepository.deleteAllByChatRoomId(roomId);
    log.info("[ChatRoomUserCommandService] 채팅방 삭제 완료 - roomId: {}, requesterId: {}", roomId, requesterId);
  }
}