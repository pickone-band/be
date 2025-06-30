package com.pickone.domain.messaging.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.policy.OwnershipPolicy;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatRoomUserCommandServiceTest {

  @InjectMocks
  private ChatRoomUserCommandService service;

  @Mock
  private ChatRoomUserRepository chatRoomUserRepository;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private UserJpaRepository userJpaRepository;

  @Mock
  private OwnershipPolicy ownershipPolicy;

  @Test
  void inviteUser_success() {
    // given
    Long roomId = 1L, inviterId = 2L, targetUserId = 3L;
    ChatRoomEntity room = new ChatRoomEntity("room");

    given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(room));
    given(userJpaRepository.findById(targetUserId)).willReturn(Optional.of(mock(UserEntity.class)));
    given(chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)).willReturn(false);

    // when
    assertDoesNotThrow(() -> service.inviteUser(roomId, inviterId, targetUserId));

    // then
    verify(chatRoomUserRepository).save(any(ChatRoomUserEntity.class));
  }
}
