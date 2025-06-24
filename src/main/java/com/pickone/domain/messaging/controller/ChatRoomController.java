package com.pickone.domain.messaging.controller;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.dto.ReadCountDto;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.service.ChatRoomService;
import com.pickone.domain.messaging.service.MessageReadService;
import com.pickone.domain.messaging.service.MessageService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.security.model.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
@Tag(name = "채팅 API", description = "채팅방 생성, 조회, 메시지 송수신, 읽음 처리")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final MessageService messageService;
  private final MessageReadService messageReadService;

  @GetMapping
  @Operation(summary = "참여 채팅방 목록", description = "현재 사용자가 참여 중인 채팅방 목록 및 최근 메시지를 조회")
  public ResponseEntity<BaseResponse<List<ChatRoomSummaryDto>>> getChatRooms(
      @AuthenticationPrincipal UserPrincipal principal
  ) {
    log.info("채팅방 목록 조회: userId={}", principal.getUserId());
    return BaseResponse.success(
        chatRoomService.getChatRoomsWithLatestMessage(principal.getUserId()));
  }

  @PostMapping
  @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성")
  public ResponseEntity<BaseResponse<ChatRoomDetailDto>> createRoom(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestBody CreateChatRoomRequest request
  ) {
    log.info("채팅방 생성 요청: userId={}, roomName={}, participants={}", principal.getUserId(),
        request.name(), request.participantIds());
    return BaseResponse.success(chatRoomService.createRoom(principal.getUserId(), request));
  }

  @PostMapping("/{roomId}/invite")
  @Operation(summary = "채팅방 초대", description = "지정한 사용자를 기존 채팅방에 초대")
  public ResponseEntity<BaseResponse<Void>> inviteUser(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long roomId,
      @RequestParam Long targetUserId
  ) {
    log.info("채팅방 초대 요청: roomId={}, inviterId={}, targetUserId={}", roomId, principal.getUserId(),
        targetUserId);
    chatRoomService.inviteUser(roomId, principal.getUserId(), targetUserId);
    return BaseResponse.success();
  }

  @DeleteMapping("/{roomId}")
  @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제")
  public ResponseEntity<BaseResponse<Void>> deleteRoom(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long roomId
  ) {
    log.info("채팅방 삭제 요청: roomId={}, userId={}", roomId, principal.getUserId());
    chatRoomService.deleteRoom(roomId, principal.getUserId());
    return BaseResponse.success();
  }

  @GetMapping("/{roomId}/messages")
  @Operation(summary = "채팅 메시지 목록", description = "채팅방 내 저장된 메시지들을 시간순으로 조회")
  public ResponseEntity<BaseResponse<List<MessageDocument>>> getMessages(
      @PathVariable Long roomId
  ) {
    log.info("채팅 메시지 조회 요청: roomId={}", roomId);
    return BaseResponse.success(messageService.getMessagesByRoom(roomId));
  }

  @PostMapping("/messages/{messageId}/read")
  @Operation(summary = "메시지 읽음 처리", description = "특정 메시지를 읽음으로 표시")
  public ResponseEntity<BaseResponse<Void>> markAsRead(
      @AuthenticationPrincipal(expression = "userId") Long userId,
      @PathVariable String messageId
  ) {
    log.info("메시지 읽음 처리 요청: messageId={}, userId={}", messageId, userId);
    messageReadService.markAsRead(messageId, userId);
    return BaseResponse.success();
  }

  @GetMapping("/messages/{messageId}/read-status")
  @Operation(summary = "읽음 통계 조회", description = "해당 메시지에 대해 읽은 인원 수 조회")
  public ResponseEntity<BaseResponse<ReadCountDto>> getReadStatus(
      @PathVariable String messageId,
      @RequestParam Long roomId
  ) {
    log.info("읽음 통계 조회 요청: messageId={}, roomId={}", messageId, roomId);
    return BaseResponse.success(messageReadService.getReadStatus(messageId, roomId));
  }
}