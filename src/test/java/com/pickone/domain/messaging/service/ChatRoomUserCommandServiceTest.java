package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.model.entity.ChatRole;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.model.policy.OwnershipPolicy;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRoomUserCommandServiceTest {

  @Mock private ChatRoomUserRepository chatRoomUserRepository;
  @Mock private ChatRoomRepository chatRoomRepository;
  @Mock private UserJpaRepository userJpaRepository;
  @Mock private OwnershipPolicy ownershipPolicy;

  @InjectMocks private ChatRoomUserCommandService sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("inviteUser")
  class InviteUser {

    @Test
    @DisplayName("정상적으로 초대 성공 (중복 아님)")
    void inviteUser_success() {
      Long roomId = 1L, inviterId = 10L, targetUserId = 20L;

      doNothing().when(ownershipPolicy).checkOwner(inviterId, roomId);
      when(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)).thenReturn(false);

      ChatRoomEntity room = mock(ChatRoomEntity.class);
      UserEntity target = mock(UserEntity.class);

      when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
      when(userJpaRepository.findById(targetUserId)).thenReturn(Optional.of(target));
      when(chatRoomUserRepository.save(any(ChatRoomUserEntity.class))).thenReturn(mock(ChatRoomUserEntity.class));

      sut.inviteUser(roomId, inviterId, targetUserId);

      verify(ownershipPolicy).checkOwner(inviterId, roomId);
      verify(chatRoomUserRepository).existsByUserIdAndChatRoomId(targetUserId, roomId);
      verify(chatRoomRepository).findById(roomId);
      verify(userJpaRepository).findById(targetUserId);
      verify(chatRoomUserRepository).save(any(ChatRoomUserEntity.class));
    }

    @Test
    @DisplayName("이미 방에 있으면 아무것도 하지 않음")
    void inviteUser_alreadyInRoom() {
      Long roomId = 1L, inviterId = 10L, targetUserId = 20L;

      doNothing().when(ownershipPolicy).checkOwner(inviterId, roomId);
      when(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)).thenReturn(true);

      sut.inviteUser(roomId, inviterId, targetUserId);

      verify(ownershipPolicy).checkOwner(inviterId, roomId);
      verify(chatRoomUserRepository).existsByUserIdAndChatRoomId(targetUserId, roomId);
      verifyNoMoreInteractions(chatRoomRepository, userJpaRepository, chatRoomUserRepository);
    }

    @Test
    @DisplayName("채팅방이 없으면 예외")
    void inviteUser_roomNotFound() {
      Long roomId = 1L, inviterId = 10L, targetUserId = 20L;

      doNothing().when(ownershipPolicy).checkOwner(inviterId, roomId);
      when(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)).thenReturn(false);
      when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.inviteUser(roomId, inviterId, targetUserId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("유저가 없으면 예외")
    void inviteUser_userNotFound() {
      Long roomId = 1L, inviterId = 10L, targetUserId = 20L;

      doNothing().when(ownershipPolicy).checkOwner(inviterId, roomId);
      when(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)).thenReturn(false);
      ChatRoomEntity room = mock(ChatRoomEntity.class);
      when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
      when(userJpaRepository.findById(targetUserId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.inviteUser(roomId, inviterId, targetUserId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }
  }

  @Nested
  @DisplayName("deleteRoom")
  class DeleteRoom {
    @Test
    @DisplayName("정상적으로 방 삭제")
    void deleteRoom_success() {
      Long roomId = 1L, requesterId = 10L;

      doNothing().when(ownershipPolicy).checkOwner(requesterId, roomId);
      doNothing().when(chatRoomUserRepository).deleteAllByChatRoomId(roomId);

      sut.deleteRoom(roomId, requesterId);

      verify(ownershipPolicy).checkOwner(requesterId, roomId);
      verify(chatRoomUserRepository).deleteAllByChatRoomId(roomId);
    }
  }
}
