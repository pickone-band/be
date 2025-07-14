package com.pickone.domain.messaging.controller;

import com.pickone.domain.messaging.dto.MessageResponse;
import com.pickone.domain.messaging.dto.SendMessageRequest;
import com.pickone.domain.messaging.service.MessageCommandService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompMessageController {

  private final MessageCommandService messageCommandService;
  private final SimpMessagingTemplate messagingTemplate;

  @MessageMapping("/chat/send")
  public void sendMessage(
      @Payload SendMessageRequest request,
      Principal principal
  ) {
    Long senderId = Long.parseLong(principal.getName()); // or 사용자 ID 파싱 방식에 따라 적절히 수정
    log.info("[StompMessageController] 메시지 전송 요청 - senderId: {}, roomId: {}", senderId,
        request.roomId());
    MessageResponse response = messageCommandService.sendMessage(senderId, request);
    messagingTemplate.convertAndSend("/topic/room." + request.roomId(), response);
  }
}