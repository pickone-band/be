package com.pickone.domain.messaging.controller;

import com.pickone.domain.messaging.dto.MessageResponse;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.service.MessageQueryService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms/{roomId}/messages")
@Tag(name = "Message", description = "채팅방 메시지 조회 API")
public class MessageQueryController {

  private final MessageQueryService messageQueryService;
  private final ChatRoomUserRepository chatRoomUserRepository;

  @Operation(summary = "메시지 목록 조회", description = "지정된 채팅방의 모든 메시지를 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<MessageResponse>>> getMessages(
      @Parameter(hidden = true) @AuthenticationPrincipal(expression = "id") Long userId,
      @Parameter(description = "채팅방 ID", example = "1") @PathVariable Long roomId
  ) {
    log.info("[MessageQueryController] 메시지 목록 조회 요청 - userId: {}, roomId: {}", userId, roomId);

    if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(userId, roomId)) {
      throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }

    return BaseResponse.success(messageQueryService.getMessages(roomId));
  }
}