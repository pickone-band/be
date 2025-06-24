package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChatRoomUserServiceTest {

    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;

    @InjectMocks
    private ChatRoomUserService chatRoomUserService;

    @Test
    void isUserInRoom_returnsTrue() {
        given(chatRoomUserRepository.existsByUserIdAndChatRoomId(1L, 10L)).willReturn(true);

        assertTrue(chatRoomUserService.isUserInRoom(1L, 10L));
    }

    @Test
    void getParticipation_success() {
        ChatRoomUserEntity cru = mock(ChatRoomUserEntity.class);
        given(chatRoomUserRepository.findByUserIdAndChatRoomId(1L, 10L)).willReturn(Optional.of(cru));

        ChatRoomUserEntity result = chatRoomUserService.getParticipation(1L, 10L);
        assertNotNull(result);
    }

    @Test
    void countParticipants_returnsCorrect() {
        given(chatRoomUserRepository.countByChatRoomId(10L)).willReturn(5L);

        assertEquals(5L, chatRoomUserService.countParticipants(10L));
    }

    @Test
    void getParticipants_success() {
        List<ChatRoomUserEntity> list = List.of(mock(ChatRoomUserEntity.class));
        given(chatRoomUserRepository.findByChatRoomId(10L)).willReturn(list);

        assertEquals(1, chatRoomUserService.getParticipants(10L).size());
    }
}
