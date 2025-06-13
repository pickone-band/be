package com.PickOne.domain.messaging.controller;

import com.PickOne.domain.messaging.dto.ReadCountDto;
import com.PickOne.domain.messaging.model.document.MessageDocument;
import com.PickOne.domain.messaging.service.MessageService;
import com.PickOne.domain.messaging.service.MessageReadService;
import com.PickOne.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "쪽지 API", description = "1:1 쪽지 송수신 및 조회")
class MessageController {

    private final MessageService messageService;
    private final MessageReadService messageReadService;

    @Operation(summary = "쪽지 전송", description = "지정한 사용자에게 쪽지를 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<BaseResponse<MessageDocument>> sendMessage(
            @AuthenticationPrincipal(expression = "id") Long senderId,
            @RequestParam Long roomId,
            @RequestParam String content
    ) {
        MessageDocument message = messageService.sendMessage(roomId, senderId, content);
        return BaseResponse.success(message);
    }

    @Operation(summary = "채팅방 메시지 목록", description = "채팅방 내 전체 메시지를 시간순으로 조회합니다.")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<BaseResponse<List<MessageDocument>>> getMessages(
            @PathVariable Long roomId
    ) {
        List<MessageDocument> messages = messageService.getMessagesByRoom(roomId);
        return BaseResponse.success(messages);
    }

    @Operation(summary = "쪽지 읽음 처리", description = "특정 메시지를 읽음으로 표시합니다.")
    @PostMapping("/{messageId}/read")
    public ResponseEntity<BaseResponse<Void>> markAsRead(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable String messageId
    ) {
        messageReadService.markAsRead(messageId, userId);
        return BaseResponse.success();
    }

    @Operation(summary = "쪽지 읽음 통계", description = "메시지별 읽음 수와 전체 인원 수를 조회합니다.")
    @GetMapping("/{messageId}/read-status")
    public ResponseEntity<BaseResponse<ReadCountDto>> getReadStatus(
            @PathVariable String messageId,
            @RequestParam Long roomId
    ) {
        return BaseResponse.success(messageReadService.getReadStatus(messageId, roomId));
    }
}
