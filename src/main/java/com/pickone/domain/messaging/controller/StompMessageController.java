package com.pickone.domain.messaging.controller;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.dto.SendMessageRequest;
import com.pickone.domain.messaging.service.MessageCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class StompMessageController {

  private final MessageCommandService messageCommandService;

  @MessageMapping("/chat/send")
  @SendTo("/topic/room.{roomId}")
  public MessageDto sendMessage(
      @Payload SendMessageRequest request,
      @Header("userId") Long senderId
  ) {
    return messageCommandService.sendMessage(senderId, request);
  }
}
