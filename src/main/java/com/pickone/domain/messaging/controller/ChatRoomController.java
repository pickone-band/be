package com.pickone.domain.messaging.controller;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.service.ChatRoomService;
import com.pickone.domain.messaging.service.ChatRoomUserCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
@Tag(name = "ChatRoom", description = "채팅방 생성, 조회, 초대, 삭제 관련 API")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final ChatRoomUserCommandService chatRoomUserCommandService;

  @Operation(
      summary = "채팅방 생성",
      description = "새로운 채팅방을 생성하고 방 정보를 반환합니다."
  )
  @PostMapping
  public ResponseEntity<ChatRoomDetailDto> createRoom(
      @Parameter(description = "채팅방 생성자 ID", required = true)
      @RequestHeader("userId") Long creatorId,

      @Parameter(description = "채팅방 생성 요청 정보", required = true)
      @RequestBody CreateChatRoomRequest request) {
    return ResponseEntity.ok(chatRoomService.createRoom(creatorId, request));
  }

  @Operation(
      summary = "사용자 초대",
      description = "특정 채팅방에 사용자를 초대합니다."
  )
  @PostMapping("/{roomId}/invite")
  public ResponseEntity<Void> inviteUser(
      @Parameter(description = "초대자 ID", required = true)
      @RequestHeader("userId") Long inviterId,

      @Parameter(description = "채팅방 ID", required = true)
      @PathVariable Long roomId,

      @Parameter(description = "초대 대상 사용자 ID", required = true)
      @RequestParam Long targetUserId) {
    chatRoomUserCommandService.inviteUser(roomId, inviterId, targetUserId);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "채팅방 삭제",
      description = "지정된 채팅방을 삭제합니다."
  )
  @DeleteMapping("/{roomId}")
  public ResponseEntity<Void> deleteRoom(
      @Parameter(description = "요청 사용자 ID", required = true)
      @RequestHeader("userId") Long userId,

      @Parameter(description = "삭제할 채팅방 ID", required = true)
      @PathVariable Long roomId) {
    chatRoomUserCommandService.deleteRoom(roomId, userId);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "참여 중인 채팅방 목록 조회",
      description = "사용자가 참여 중인 채팅방 목록과 각 채팅방의 최신 메시지를 조회합니다."
  )
  @GetMapping
  public ResponseEntity<List<ChatRoomSummaryDto>> getChatRooms(
      @Parameter(description = "요청 사용자 ID", required = true)
      @RequestHeader("userId") Long userId) {
    return ResponseEntity.ok(chatRoomService.getChatRoomsWithLatestMessage(userId));
  }
}
