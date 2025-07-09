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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatRoomUserCommandServiceTest {

  @InjectMocks
  private ChatRoomUserCommandService chatRoomUserCommandService;

  @Mock
  private ChatRoomUserRepository chatRoomUserRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private UserJpaRepository userJpaRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void inviteUser_success() {
    // given
    Long roomId = 1L;
    Long inviterId = 10L;
    Long targetUserId = 20L;

    ChatRoom room = mock(ChatRoom.class);
    ChatRoomUser inviter = mock(ChatRoomUser.class);
    User targetUser = mock(User.class);

    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(chatRoomUserRepository.findByUserIdAndChatRoomId(inviterId, roomId))
        .thenReturn(Optional.of(inviter));
    when(inviter.isOwner()).thenReturn(true);
    when(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId))
        .thenReturn(false);
    when(userJpaRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

    // when
    chatRoomUserCommandService.inviteUser(roomId, inviterId, targetUserId);

    // then
    verify(chatRoomUserRepository).save(any(ChatRoomUser.class));
  }

  @Test
  void inviteUser_fail_notOwner() {
    // given
    Long roomId = 1L;
    Long inviterId = 10L;
    Long targetUserId = 20L;

    ChatRoom room = mock(ChatRoom.class);
    ChatRoomUser inviter = mock(ChatRoomUser.class);

    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(chatRoomUserRepository.findByUserIdAndChatRoomId(inviterId, roomId))
        .thenReturn(Optional.of(inviter));
    when(inviter.isOwner()).thenReturn(false);

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> chatRoomUserCommandService.inviteUser(roomId, inviterId, targetUserId));
    assertEquals(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN, ex.getErrorCode());
    verify(chatRoomUserRepository, never()).save(any(ChatRoomUser.class));
  }

  @Test
  void deleteRoom_success() {
    // given
    Long roomId = 1L;
    Long requesterId = 10L;

    ChatRoomUser requester = mock(ChatRoomUser.class);

    when(chatRoomUserRepository.findByUserIdAndChatRoomId(requesterId, roomId))
        .thenReturn(Optional.of(requester));
    when(requester.isOwner()).thenReturn(true);

    // when
    chatRoomUserCommandService.deleteRoom(roomId, requesterId);

    // then
    verify(chatRoomUserRepository).deleteAllByChatRoomId(roomId);
  }

  @Test
  void deleteRoom_fail_notOwner() {
    // given
    Long roomId = 1L;
    Long requesterId = 10L;

    ChatRoomUser requester = mock(ChatRoomUser.class);

    when(chatRoomUserRepository.findByUserIdAndChatRoomId(requesterId, roomId))
        .thenReturn(Optional.of(requester));
    when(requester.isOwner()).thenReturn(false);

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> chatRoomUserCommandService.deleteRoom(roomId, requesterId));
    assertEquals(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN, ex.getErrorCode());
    verify(chatRoomUserRepository, never()).deleteAllByChatRoomId(roomId);
  }

  @Test
  void inviteUser_noDuplicate() {
    // given
    Long roomId = 1L;
    Long inviterId = 10L;
    Long targetUserId = 20L;

    ChatRoom room = mock(ChatRoom.class);
    ChatRoomUser inviter = mock(ChatRoomUser.class);

    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(chatRoomUserRepository.findByUserIdAndChatRoomId(inviterId, roomId))
        .thenReturn(Optional.of(inviter));
    when(inviter.isOwner()).thenReturn(true);
    when(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId))
        .thenReturn(true);  // 이미 존재하는 사용자

    // when
    chatRoomUserCommandService.inviteUser(roomId, inviterId, targetUserId);

    // then
    verify(chatRoomUserRepository, never()).save(any(ChatRoomUser.class));
  }

  @Test
  void inviteUser_fail_targetUserNotFound() {
    // given
    Long roomId = 1L;
    Long inviterId = 10L;
    Long targetUserId = 20L;

    ChatRoom room = mock(ChatRoom.class);
    ChatRoomUser inviter = mock(ChatRoomUser.class);

    when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
    when(chatRoomUserRepository.findByUserIdAndChatRoomId(inviterId, roomId))
        .thenReturn(Optional.of(inviter));
    when(inviter.isOwner()).thenReturn(true);
    when(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId))
        .thenReturn(false);
    when(userJpaRepository.findById(targetUserId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> chatRoomUserCommandService.inviteUser(roomId, inviterId, targetUserId));
    assertEquals(ErrorCode.USER_INFO_NOT_FOUND, ex.getErrorCode());

    verify(chatRoomUserRepository, never()).save(any(ChatRoomUser.class));
  }
}
