package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.model.entity.ChatRole;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;

    @Mock
    private MessageMongoRepository messageMongoRepository;

    @Mock
    private MessageAggregationRepository messageAggregationRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @Test
    void createRoom_success() {
        // given
        Long creatorId = 1L;
        List<Long> participantIds = List.of(2L, 3L);

        CreateChatRoomRequest request = new CreateChatRoomRequest("테스트방", participantIds);

        UserEntity creator = UserEntity.builder()
                .email("creator@test.com")
                .password("encoded")
                .nickname("크리에이터")
                .profileImage(null)
                .role(Role.USER)
                .isPublic(true)
                .isOauth(false)
                .gender(Gender.MALE)
                .birthDate(LocalDate.now())
                .mbti(null)
                .genres(List.of())
                .build();

        UserEntity participant1 = UserEntity.builder()
                .email("user1@test.com")
                .password("encoded")
                .nickname("유저1")
                .profileImage(null)
                .role(Role.USER)
                .isPublic(true)
                .isOauth(false)
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.now())
                .mbti(null)
                .genres(List.of())
                .build();

        UserEntity participant2 = UserEntity.builder()
                .email("user2@test.com")
                .password("encoded")
                .nickname("유저2")
                .profileImage(null)
                .role(Role.USER)
                .isPublic(true)
                .isOauth(false)
                .gender(Gender.MALE)
                .birthDate(LocalDate.now())
                .mbti(null)
                .genres(List.of())
                .build();

        given(userJpaRepository.findById(creatorId)).willReturn(Optional.of(creator));
        given(userJpaRepository.findAllById(participantIds)).willReturn(List.of(participant1, participant2));

        // when
        ChatRoomDetailDto result = chatRoomService.createRoom(creatorId, request);

        // then
        assertEquals("테스트방", result.name());
        assertTrue(result.participantNicknames().contains("크리에이터"));
        assertTrue(result.participantNicknames().contains("유저1"));
        assertTrue(result.participantNicknames().contains("유저2"));

        verify(chatRoomRepository).save(any(ChatRoomEntity.class));
        verify(chatRoomUserRepository).saveAll(anyList());
    }

    @Test
    void getChatRoomsWithLatestMessage_success() {
        // given
        Long userId = 1L;

        UserEntity user = UserEntity.builder()
                .email("creator@test.com")
                .password(null)
                .nickname("크리에이터")
                .profileImage(null)
                .role(Role.USER)
                .isPublic(true)
                .isOauth(false)
                .gender(Gender.MALE)
                .birthDate(LocalDate.now())
                .mbti(null)
                .genres(List.of())
                .build();


        ChatRoomEntity room1 = new ChatRoomEntity(100L, "방1", new ArrayList<>());
        ChatRoomEntity room2 = new ChatRoomEntity(200L, "방2", new ArrayList<>());

        ChatRoomUserEntity cru1 = new ChatRoomUserEntity(null, room1, user, ChatRole.MEMBER);
        ChatRoomUserEntity cru2 = new ChatRoomUserEntity(null, room2, user, ChatRole.MEMBER);

        List<ChatRoomUserEntity> participation = List.of(cru1, cru2);

        MessageDocument msg1 = new MessageDocument("m1", 100L, 1L, "하이", LocalDateTime.now());
        MessageDocument msg2 = new MessageDocument("m2", 200L, 1L, "헬로", LocalDateTime.now());

        given(chatRoomUserRepository.findByUserId(userId)).willReturn(participation);
        given(messageAggregationRepository.findLatestMessagesPerRoom(List.of(100L, 200L)))
                .willReturn(List.of(msg1, msg2));

        // when
        List<ChatRoomSummaryDto> result = chatRoomService.getChatRoomsWithLatestMessage(userId);

        // then
        assertEquals(2, result.size());
        assertEquals("하이", result.get(0).lastMessage());
        assertEquals("헬로", result.get(1).lastMessage());
    }

    @Test
    void deleteRoom_success_byOwner() {
        // given
        Long roomId = 10L;
        Long requesterId = 1L;

        ChatRoomEntity room = new ChatRoomEntity("삭제할방");

        ChatRoomUserEntity ownerParticipation = new ChatRoomUserEntity(room, mock(UserEntity.class), ChatRole.OWNER);

        given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(room));
        given(chatRoomUserRepository.findByUserIdAndChatRoomId(requesterId, roomId)).willReturn(Optional.of(ownerParticipation));

        // when
        assertDoesNotThrow(() -> chatRoomService.deleteRoom(roomId, requesterId));

        // then
        verify(chatRoomUserRepository).deleteAllByChatRoomId(roomId);
        verify(chatRoomRepository).delete(room);
    }

    @Test
    void deleteRoom_fail_ifNotOwner() {
        // given
        Long roomId = 10L;
        Long requesterId = 1L;

        ChatRoomEntity room = new ChatRoomEntity("삭제 불가 방");
        ChatRoomUserEntity notOwnerParticipation = new ChatRoomUserEntity(room, mock(UserEntity.class), ChatRole.MEMBER);

        given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(room));
        given(chatRoomUserRepository.findByUserIdAndChatRoomId(requesterId, roomId)).willReturn(Optional.of(notOwnerParticipation));

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            chatRoomService.deleteRoom(roomId, requesterId);
        });
        assertEquals(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void deleteRoom_fail_ifNotParticipant() {
        // given
        Long roomId = 10L;
        Long requesterId = 1L;
        ChatRoomEntity room = new ChatRoomEntity("삭제할방");

        given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(room));
        given(chatRoomUserRepository.findByUserIdAndChatRoomId(requesterId, roomId)).willReturn(Optional.empty());

        // when & then
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            chatRoomService.deleteRoom(roomId, requesterId);
        });
        assertEquals(ErrorCode.CHAT_ROOM_ACCESS_DENIED, ex.getErrorCode());
    }
}