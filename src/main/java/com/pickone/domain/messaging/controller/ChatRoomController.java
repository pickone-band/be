package com.pickone.domain.messaging.controller;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.service.ChatRoomService;
import com.pickone.domain.messaging.service.ChatRoomUserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;
  private final ChatRoomUserCommandService chatRoomUserCommandService;

  @PostMapping
  public ResponseEntity<ChatRoomDetailDto> createRoom(
      @RequestHeader("userId") Long creatorId,
      @RequestBody CreateChatRoomRequest request) {
    return ResponseEntity.ok(chatRoomService.createRoom(creatorId, request));
  }

  @PostMapping("/{roomId}/invite")
  public ResponseEntity<Void> inviteUser(
      @RequestHeader("userId") Long inviterId,
      @PathVariable Long roomId,
      @RequestParam Long targetUserId) {
    chatRoomUserCommandService.inviteUser(roomId, inviterId, targetUserId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{roomId}")
  public ResponseEntity<Void> deleteRoom(
      @RequestHeader("userId") Long userId,
      @PathVariable Long roomId) {
    chatRoomUserCommandService.deleteRoom(roomId, userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<ChatRoomSummaryDto>> getChatRooms(
      @RequestHeader("userId") Long userId) {
    return ResponseEntity.ok(chatRoomService.getChatRoomsWithLatestMessage(userId));
  }
}
