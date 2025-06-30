package com.pickone.domain.messaging.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.factory.ChatRoomFactory;
import com.pickone.domain.messaging.mapper.ChatRoomDtoMapper;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

  @InjectMocks
  private ChatRoomService chatRoomService;

  @Mock
  private ChatRoomRepository chatRoomRepository;

  @Mock
  private ChatRoomUserRepository chatRoomUserRepository;

  @Mock
  private UserJpaRepository userJpaRepository;

  @Mock
  private MessageAggregationRepository messageAggregationRepository;

  @Mock
  private ChatRoomFactory chatRoomFactory;

  @Mock
  private ChatRoomDtoMapper chatRoomDtoMapper;

  @Test
  void createRoom_success() {
    // given
    Long creatorId = 1L;
    CreateChatRoomRequest request = new CreateChatRoomRequest("study", List.of(2L, 3L));
    UserEntity creator = mock(UserEntity.class);
    List<UserEntity> participants = List.of(mock(UserEntity.class), mock(UserEntity.class));
    ChatRoomEntity room = new ChatRoomEntity("study");

    given(userJpaRepository.findById(creatorId)).willReturn(Optional.of(creator));
    given(userJpaRepository.findAllById(request.participantIds())).willReturn(participants);
    given(chatRoomFactory.create(request.name())).willReturn(room);
    given(chatRoomDtoMapper.toDetailDto(eq(room), anyList())).willReturn(
        new ChatRoomDetailDto(room.getId(), room.getName(), List.of("a", "b", "c"))
    );

    // when
    ChatRoomDetailDto result = chatRoomService.createRoom(creatorId, request);

    // then
    assertNotNull(result);
    verify(chatRoomRepository).save(room);
    verify(chatRoomUserRepository).saveAll(anyList());
  }

  @Test
  void getChatRoomsWithLatestMessage_success() {
    // given
    Long userId = 1L;

    ChatRoomEntity room = mock(ChatRoomEntity.class);
    when(room.getId()).thenReturn(100L);
    when(room.getName()).thenReturn("room");

    ChatRoomUserEntity cru = mock(ChatRoomUserEntity.class);
    when(cru.getChatRoom()).thenReturn(room);

    given(chatRoomUserRepository.findByUserId(userId)).willReturn(List.of(cru));

    MessageDocument msg = new MessageDocument("id", 100L, 1L, "hi", LocalDateTime.now());
    given(messageAggregationRepository.findLatestMessagesPerRoom(List.of(100L)))
        .willReturn(List.of(msg));

    // when
    List<ChatRoomSummaryDto> results = chatRoomService.getChatRoomsWithLatestMessage(userId);

    // then
    assertFalse(results.isEmpty());
    assertEquals("hi", results.get(0).lastMessage());
  }
}
