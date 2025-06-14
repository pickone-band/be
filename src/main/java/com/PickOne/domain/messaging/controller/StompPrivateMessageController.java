package com.PickOne.domain.messaging.controller;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.service.ChatRoomService;
import com.PickOne.domain.messaging.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompPrivateMessageController {

    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages/private")
    public void handlePrivateMessage(@Payload MessageDto message) {
        log.debug("[STOMP] 1:1 메시지 수신: {}", message);
        chatRoomService.validateUserInRoom(message.roomId(), message.senderId());
        var saved = messageService.sendMessage(message.roomId(), message.senderId(), message.content());

        MessageDto broadcast = new MessageDto(
                saved.getId(),
                saved.getRoomId(),
                saved.getSenderId(),
                message.recipientId(),
                saved.getContent(),
                "SENT",
                saved.getSentAt(),
                null,
                null
        );

        // 바로 STOMP로 전송 (Redis 없이)
        messagingTemplate.convertAndSendToUser(
                broadcast.recipientId().toString(),
                "/queue/messages",
                broadcast
        );
    }
}