package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.model.document.MessageDocument;
import com.PickOne.domain.messaging.repository.MessageMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageMongoRepository messageMongoRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void sendMessage_success() {
        // given
        Long roomId = 1L;
        Long senderId = 10L;
        String content = "안녕하세요";
        LocalDateTime now = LocalDateTime.now();

        MessageDocument saved = new MessageDocument("m1", roomId, senderId, content, now);
        given(messageMongoRepository.save(any())).willReturn(saved);

        // when
        MessageDocument result = messageService.sendMessage(roomId, senderId, content);

        // then
        assertEquals(roomId, result.getRoomId());
        assertEquals(senderId, result.getSenderId());
        assertEquals(content, result.getContent());
    }

    @Test
    void getMessagesByRoom_success() {
        // given
        Long roomId = 1L;
        List<MessageDocument> messages = List.of(
                new MessageDocument("m1", roomId, 1L, "msg1", LocalDateTime.now())
        );
        given(messageMongoRepository.findByRoomIdOrderBySentAtAsc(roomId)).willReturn(messages);

        // when
        List<MessageDocument> result = messageService.getMessagesByRoom(roomId);

        // then
        assertEquals(1, result.size());
    }

    @Test
    void getLastMessage_success() {
        // given
        Long roomId = 1L;
        MessageDocument lastMessage = new MessageDocument("m2", roomId, 2L, "마지막 메시지", LocalDateTime.now());
        given(messageMongoRepository.findTopByRoomIdOrderBySentAtDesc(roomId)).willReturn(lastMessage);

        // when
        MessageDocument result = messageService.getLastMessage(roomId);

        // then
        assertEquals("마지막 메시지", result.getContent());
    }
}