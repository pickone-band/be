package com.PickOne.domain.messaging.controller;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.service.MessagingService;
import com.PickOne.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    /**
     * 메시지 전송을 위한 REST 엔드포인트
     */
    @Operation(summary = "메시지 전송", description = "특정 사용자에게 메시지를 전송합니다.")
    @PostMapping("/send/{recipientId}")
    public ResponseEntity<BaseResponse<MessageDto>> sendMessage(
            @PathVariable Long recipientId,
            @RequestBody String content) {

        Long currentUserId = getCurrentUserId();
        Message message = messagingService.sendMessage(currentUserId, recipientId, content);

        return BaseResponse.success(MessageDto.fromDomain(message));
    }

    /**
     * 다른 사용자와의 대화를 조회하는 REST 엔드포인트
     */
    @Operation(summary = "대화 내용 조회", description = "특정 사용자와의 대화 내용을 조회합니다.")
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<BaseResponse<Page<MessageDto>>> getConversation(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long currentUserId = getCurrentUserId();
        Page<Message> conversation = messagingService.getConversation(currentUserId, userId, pageable);

        Page<MessageDto> messageDtos = conversation.map(MessageDto::fromDomain);
        return BaseResponse.success(messageDtos);
    }

    /**
     * 메시지를 읽음 상태로 표시하는 REST 엔드포인트
     */
    @Operation(summary = "메시지 읽음 표시", description = "메시지를 읽음 상태로 표시합니다.")
    @PostMapping("/{messageId}/read")
    public ResponseEntity<BaseResponse<MessageDto>> markMessageRead(
            @PathVariable String messageId) {

        Message message = messagingService.markMessageRead(messageId);
        return BaseResponse.success(MessageDto.fromDomain(message));
    }

    /**
     * 읽지 않은 메시지를 조회하는 REST 엔드포인트
     */
    @Operation(summary = "읽지 않은 메시지 조회", description = "읽지 않은 메시지를 조회합니다.")
    @GetMapping("/unread")
    public ResponseEntity<BaseResponse<List<MessageDto>>> getUnreadMessages() {
        Long currentUserId = getCurrentUserId();
        List<Message> unreadMessages = messagingService.getUnreadMessages(currentUserId);

        List<MessageDto> messageDtos = unreadMessages.stream()
                .map(MessageDto::fromDomain)
                .collect(Collectors.toList());

        return BaseResponse.success(messageDtos);
    }

    /**
     * 읽지 않은 메시지 수를 조회하는 REST 엔드포인트
     */
    @Operation(summary = "읽지 않은 메시지 수 조회", description = "읽지 않은 메시지의 수를 조회합니다.")
    @GetMapping("/unread/count")
    public ResponseEntity<BaseResponse<Long>> getUnreadMessageCount() {
        Long currentUserId = getCurrentUserId();
        long count = messagingService.countUnreadMessages(currentUserId);

        return BaseResponse.success(count);
    }

    /**
     * 최근 대화 목록을 조회하는 REST 엔드포인트
     */
    @Operation(summary = "최근 대화 목록 조회", description = "최근 대화 목록을 조회합니다.")
    @GetMapping("/recent")
    public ResponseEntity<BaseResponse<List<MessageDto>>> getRecentConversations() {
        Long currentUserId = getCurrentUserId();
        List<Message> recentConversations = messagingService.getRecentConversations(currentUserId);

        List<MessageDto> messageDtos = recentConversations.stream()
                .map(MessageDto::fromDomain)
                .collect(Collectors.toList());

        return BaseResponse.success(messageDtos);
    }

    /**
     * 메시지 전송을 위한 WebSocket 엔드포인트
     */
    @MessageMapping("/message.send")
    public void handleSendMessage(@Payload MessageDto messageDto, Principal principal) {
        Long senderId = Long.valueOf(principal.getName());
        messagingService.sendMessage(senderId, messageDto.recipientId(), messageDto.content());
    }

    /**
     * 메시지를 전달됨으로 표시하는 WebSocket 엔드포인트
     */
    @MessageMapping("/message.delivered")
    public void handleMessageDelivered(@Payload String messageId) {
        messagingService.markMessageDelivered(messageId);
    }

    /**
     * 메시지를 읽음으로 표시하는 WebSocket 엔드포인트
     */
    @MessageMapping("/message.read")
    public void handleMessageRead(@Payload String messageId) {
        messagingService.markMessageRead(messageId);
    }

    /**
     * 현재 인증된 사용자의 ID를 가져옴
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName());
    }
}