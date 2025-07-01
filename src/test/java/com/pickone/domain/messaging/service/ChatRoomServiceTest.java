package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.factory.ChatRoomFactory;
import com.pickone.domain.messaging.mapper.ChatRoomDtoMapper;
import com.pickone.domain.messaging.model.entity.ChatRole;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRoomServiceTest {

  @Mock private ChatRoomRepository chatRoomRepository;
  @Mock private ChatRoomUserRepository chatRoomUserRepository;
  @Mock private UserJpaRepository userJpaRepository;
  @Mock private ChatRoomFactory chatRoomFactory;
  @Mock private ChatRoomDtoMapper chatRoomDtoMapper;
  @Mock private MessageAggregationRepository messageAggregationRepository;

  @InjectMocks
  private ChatRoomService sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class CreateRoom {

    @Test
    @DisplayName("정상적으로 채팅방 생성")
    void createRoom_success() {
      // given
      Long creatorId = 1L;
      List<Long> participantIds = List.of(2L, 3L);
      CreateChatRoomRequest req = new CreateChatRoomRequest("testRoom", participantIds);

      UserEntity creator = mock(UserEntity.class);
      UserEntity participant1 = mock(UserEntity.class);
      UserEntity participant2 = mock(UserEntity.class);

      when(userJpaRepository.findById(creatorId)).thenReturn(Optional.of(creator));
      when(userJpaRepository.findAllById(participantIds)).thenReturn(List.of(participant1, participant2));

      ChatRoomEntity room = mock(ChatRoomEntity.class);
      when(chatRoomFactory.create("testRoom")).thenReturn(room);

      when(chatRoomRepository.save(room)).thenReturn(room); // 저장 후 반환값으로 Entity 반환 가정
      when(chatRoomUserRepository.saveAll(anyList())).thenReturn(List.of(/* 생성된 엔티티들 */));

      ChatRoomDetailDto expectedDto = mock(ChatRoomDetailDto.class);
      when(chatRoomDtoMapper.toDetailDto(eq(room), anyList())).thenReturn(expectedDto);

      // when
      ChatRoomDetailDto result = sut.createRoom(creatorId, req);

      // then
      assertThat(result).isSameAs(expectedDto);

      verify(userJpaRepository).findById(creatorId);
      verify(userJpaRepository).findAllById(participantIds);
      verify(chatRoomFactory).create("testRoom");
      verify(chatRoomRepository).save(room);
      verify(chatRoomUserRepository).saveAll(anyList());
      verify(chatRoomDtoMapper).toDetailDto(eq(room), anyList());
    }

    @Test
    @DisplayName("유저 미존재시 예외")
    void createRoom_userNotFound() {
      Long creatorId = 1L;
      CreateChatRoomRequest req = new CreateChatRoomRequest("testRoom", List.of(2L, 3L));

      when(userJpaRepository.findById(creatorId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.createRoom(creatorId, req))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }
  }

  @Nested
  class FindByIdOrThrow {

    @Test
    @DisplayName("존재하면 채팅방 반환")
    void findByIdOrThrow_success() {
      Long roomId = 10L;
      ChatRoomEntity room = mock(ChatRoomEntity.class);

      when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room));

      ChatRoomEntity result = sut.findByIdOrThrow(roomId);

      assertThat(result).isSameAs(room);
      verify(chatRoomRepository).findById(roomId);
    }

    @Test
    @DisplayName("존재하지 않으면 예외")
    void findByIdOrThrow_notFound() {
      Long roomId = 10L;
      when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.findByIdOrThrow(roomId))
          .isInstanceOf(BusinessException.class)
          .hasMessageContaining(ErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
    }
  }

  @Nested
  class GetChatRoomsWithLatestMessage {

    @Test
    @DisplayName("채팅방 요약 리스트 + 최신 메시지 정상 반환")
    void getChatRoomsWithLatestMessage_success() {
      Long userId = 1L;
      ChatRoomEntity room1 = mock(ChatRoomEntity.class);
      ChatRoomEntity room2 = mock(ChatRoomEntity.class);
      when(room1.getId()).thenReturn(10L);
      when(room2.getId()).thenReturn(20L);
      when(room1.getName()).thenReturn("room10");
      when(room2.getName()).thenReturn("room20");

      ChatRoomUserEntity cu1 = mock(ChatRoomUserEntity.class);
      ChatRoomUserEntity cu2 = mock(ChatRoomUserEntity.class);

      when(cu1.getChatRoom()).thenReturn(room1);
      when(cu2.getChatRoom()).thenReturn(room2);

      List<ChatRoomUserEntity> participations = List.of(cu1, cu2);
      when(chatRoomUserRepository.findByUserId(userId)).thenReturn(participations);

      // latest messages mock
      MessageDocument msg1 = mock(MessageDocument.class);
      MessageDocument msg2 = mock(MessageDocument.class);
      when(msg1.getRoomId()).thenReturn(10L);
      when(msg1.getContent()).thenReturn("hi");
      when(msg1.getSentAt()).thenReturn(LocalDateTime.now());
      when(msg2.getRoomId()).thenReturn(20L);
      when(msg2.getContent()).thenReturn("bye");
      when(msg2.getSentAt()).thenReturn(LocalDateTime.now());

      List<MessageDocument> latestMessages = List.of(msg1, msg2);
      when(messageAggregationRepository.findLatestMessagesPerRoom(List.of(10L, 20L))).thenReturn(latestMessages);

      // when
      List<ChatRoomSummaryDto> result = sut.getChatRoomsWithLatestMessage(userId);

      // then
      assertThat(result)
          .hasSize(2)
          .allSatisfy(dto -> {
            assertThat(dto.roomId()).isIn(10L, 20L);
            assertThat(dto.roomName()).isIn("room10", "room20");
            assertThat(dto.lastMessage()).isIn("hi", "bye");
          });

      verify(chatRoomUserRepository).findByUserId(userId);
      verify(messageAggregationRepository).findLatestMessagesPerRoom(List.of(10L, 20L));
    }

    @Test
    @DisplayName("메시지 없는 채팅방은 null 반환")
    void getChatRoomsWithLatestMessage_nullMessage() {
      Long userId = 1L;
      ChatRoomEntity room1 = mock(ChatRoomEntity.class);
      when(room1.getId()).thenReturn(11L);
      when(room1.getName()).thenReturn("room11");

      ChatRoomUserEntity cu1 = mock(ChatRoomUserEntity.class);
      when(cu1.getChatRoom()).thenReturn(room1);

      List<ChatRoomUserEntity> participations = List.of(cu1);
      when(chatRoomUserRepository.findByUserId(userId)).thenReturn(participations);

      // 메시지 없음
      when(messageAggregationRepository.findLatestMessagesPerRoom(List.of(11L))).thenReturn(Collections.emptyList());

      List<ChatRoomSummaryDto> result = sut.getChatRoomsWithLatestMessage(userId);

      assertThat(result)
          .hasSize(1)
          .allSatisfy(dto -> {
            assertThat(dto.roomId()).isEqualTo(11L);
            assertThat(dto.lastMessage()).isNull();
          });
    }
  }
}
