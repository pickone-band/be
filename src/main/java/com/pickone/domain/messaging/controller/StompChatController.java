package com.pickone.domain.messaging.controller;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.service.ChatRoomService;
import com.pickone.domain.messaging.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
class StompChatController {

  private final MessageService messageService;
  private final ChatRoomService chatRoomService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/messages")
  public void handleMessage(@Payload MessageDto message) {
    log.info("[STOMP] 메시지 수신: roomId={}, senderId={}, content={}",
        message.roomId(), message.senderId(), message.content());

    chatRoomService.validateUserInRoom(message.roomId(), message.senderId());

    var saved = messageService.sendMessage(message.roomId(), message.senderId(),
        message.content());

    MessageDto broadcast = new MessageDto(
        saved.getId(), saved.getRoomId(), saved.getSenderId(),
        message.recipientId(), saved.getContent(), "SENT",
        saved.getSentAt(), null, null
    );

    log.info("[STOMP] 메시지 전송: roomId={}, senderId={}, content={}",
        broadcast.roomId(), broadcast.senderId(), broadcast.content());

    messagingTemplate.convertAndSend("/topic/room/" + broadcast.roomId(), broadcast);
  }
}