package com.PickOne.domain.messaging.controller;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.dto.SendMessageRequest;
import com.PickOne.domain.messaging.mapper.MessageMapper;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.service.MessagingService;
import com.PickOne.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "메시징 API", description = "실시간 메시징 관련 API")
public class MessageController {

    private final MessagingService messagingService;

    @Operation(summary = "메시지 전송", description = "특정 사용자에게 메시지를 전송합니다.")
    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody @Valid SendMessageRequest request) {
        Message message = messagingService.sendMessage(request.senderId(), request.recipientId(), request.content());
        return ResponseEntity.ok(MessageMapper.toDto(message));
    }

    @Operation(summary = "대화 내용 조회", description = "특정 사용자와의 대화 내용을 조회합니다.")
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDto>> getConversation(
            @RequestParam Long userId,
            @RequestParam Long otherUserId
    ) {
        List<MessageDto> conversation = messagingService.getConversation(userId, otherUserId).stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(conversation);
    }

    @Operation(summary = "읽지 않은 메시지 조회", description = "읽지 않은 메시지를 조회합니다.")
    @GetMapping("/unread")
    public ResponseEntity<List<MessageDto>> getUnreadMessages(@RequestParam Long userId) {
        List<MessageDto> messages = messagingService.getUnreadMessages(userId).stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "최근 메시지 조회", description = "최근 대화 목록을 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<List<MessageDto>> getRecentMessages(@RequestParam Long userId) {
        List<MessageDto> messages = messagingService.getRecentMessages(userId).stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "메시지 읽음 표시", description = "메시지를 읽음 상태로 표시합니다.")
    @PostMapping("/{messageId}/read")
    public ResponseEntity<MessageDto> markAsRead(@PathVariable String messageId) {
        Message updated = messagingService.markAsRead(messageId);
        return ResponseEntity.ok(MessageMapper.toDto(updated));
    }

    @Operation(summary = "메시지 삭제", description = "메시지를 삭제합니다.")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable String messageId) {
        messagingService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}