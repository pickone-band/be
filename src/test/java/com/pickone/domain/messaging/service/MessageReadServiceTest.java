package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.ReadCountDto;
import com.pickone.domain.messaging.model.document.MessageReadStatusDocument;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageReadServiceTest {

    @Mock
    private MessageReadStatusMongoRepository readRepository;

    @Mock
    private ChatRoomUserRepository chatRoomUserRepository;

    @InjectMocks
    private MessageReadService messageReadService;

    @Test
    void markAsRead_notExists_savesNew() {
        // given
        String messageId = "m1";
        Long userId = 1L;

        given(readRepository.findByMessageIdAndUserId(messageId, userId)).willReturn(Optional.empty());

        // when / then
        assertDoesNotThrow(() -> messageReadService.markAsRead(messageId, userId));
        verify(readRepository).save(any());
    }

    @Test
    void markAsRead_alreadyExists_doesNothing() {
        // given
        String messageId = "m1";
        Long userId = 1L;
        MessageReadStatusDocument doc = new MessageReadStatusDocument("r1", messageId, userId, LocalDateTime.now());

        given(readRepository.findByMessageIdAndUserId(messageId, userId)).willReturn(Optional.of(doc));

        // when / then
        assertDoesNotThrow(() -> messageReadService.markAsRead(messageId, userId));
        verify(readRepository, never()).save(any());
    }

    @Test
    void getReadStatus_correctCounts() {
        // given
        String messageId = "m1";
        Long roomId = 1L;

        MessageReadStatusDocument read = new MessageReadStatusDocument("r2", messageId, 2L, LocalDateTime.now());
        given(readRepository.findByMessageId(messageId)).willReturn(List.of(read));
        given(chatRoomUserRepository.countByChatRoomId(roomId)).willReturn(3L);

        // when
        ReadCountDto result = messageReadService.getReadStatus(messageId, roomId);

        // then
        assertEquals(1L, result.readCount());
        assertEquals(3L, result.totalCount());
    }
}
