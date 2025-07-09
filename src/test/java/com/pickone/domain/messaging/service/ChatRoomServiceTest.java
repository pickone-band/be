package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.document.Message;
import com.pickone.domain.messaging.dto.ChatRoomDetailResponse;
import com.pickone.domain.messaging.dto.ChatRoomSummaryResponse;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.entity.ChatRole;
import com.pickone.domain.messaging.entity.ChatRoom;
import com.pickone.domain.messaging.entity.ChatRoomUser;
import com.pickone.domain.messaging.mapper.ChatRoomMapper;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

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
  private ChatRoomMapper chatRoomMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createRoom_success() {
    // given
    Long creatorId = 1L;
    CreateChatRoomRequest request = new CreateChatRoomRequest("스터디룸", List.of(2L, 3L));

    User creator = mock(User.class);
    User participant1 = mock(User.class);
    User participant2 = mock(User.class);

    ChatRoom room = mock(ChatRoom.class);
    ChatRoomDetailResponse response = mock(ChatRoomDetailResponse.class);

    when(userJpaRepository.findById(creatorId)).thenReturn(Optional.of(creator));
    when(userJpaRepository.findAllById(request.participantIds())).thenReturn(List.of(participant1, participant2));
    when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(room);
    when(chatRoomMapper.toDetailResponse(any(), anyList())).thenReturn(response);

    // when
    ChatRoomDetailResponse result = chatRoomService.createRoom(creatorId, request);

    // then
    assertNotNull(result);
    verify(userJpaRepository).findById(creatorId);
    verify(userJpaRepository).findAllById(request.participantIds());
    verify(chatRoomRepository).save(any(ChatRoom.class));
    verify(chatRoomUserRepository).saveAll(anyList());
    verify(chatRoomMapper).toDetailResponse(any(), anyList());
  }

  @Test
  void createRoom_fail_userNotFound() {
    // given
    Long creatorId = 1L;
    CreateChatRoomRequest request = new CreateChatRoomRequest("스터디룸", List.of(2L, 3L));

    when(userJpaRepository.findById(creatorId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> chatRoomService.createRoom(creatorId, request));

    assertEquals(ErrorCode.USER_INFO_NOT_FOUND, ex.getErrorCode());
    verify(userJpaRepository).findById(creatorId);
    verifyNoInteractions(chatRoomRepository, chatRoomUserRepository, chatRoomMapper);
  }

  @Test
  void getChatRoomsWithLatestMessage_success() {
    // given
    Long userId = 1L;

    ChatRoom room1 = mock(ChatRoom.class);
    ChatRoom room2 = mock(ChatRoom.class);

    when(room1.getId()).thenReturn(10L);
    when(room1.getName()).thenReturn("스터디1");
    when(room2.getId()).thenReturn(20L);
    when(room2.getName()).thenReturn("스터디2");

    ChatRoomUser chatRoomUser1 = mock(ChatRoomUser.class);
    ChatRoomUser chatRoomUser2 = mock(ChatRoomUser.class);
    when(chatRoomUser1.getChatRoom()).thenReturn(room1);
    when(chatRoomUser2.getChatRoom()).thenReturn(room2);

    Message message1 = mock(Message.class);
    when(message1.getRoomId()).thenReturn(10L);
    when(message1.getContent()).thenReturn("최근 메시지1");
    when(message1.getSentAt()).thenReturn(LocalDateTime.now());

    when(chatRoomUserRepository.findByUserId(userId)).thenReturn(List.of(chatRoomUser1, chatRoomUser2));
    when(messageAggregationRepository.findLatestMessagesPerRoom(List.of(10L, 20L))).thenReturn(List.of(message1));

    // when
    List<ChatRoomSummaryResponse> results = chatRoomService.getChatRoomsWithLatestMessage(userId);

    // then
    assertEquals(2, results.size());
    assertEquals("최근 메시지1", results.get(0).lastMessage());
    assertNull(results.get(1).lastMessage());

    verify(chatRoomUserRepository).findByUserId(userId);
    verify(messageAggregationRepository).findLatestMessagesPerRoom(List.of(10L, 20L));
  }
}
