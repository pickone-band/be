package com.PickOne.domain.messaging.controller;

import com.PickOne.domain.messaging.dto.ChatRoomDetailDto;
import com.PickOne.domain.messaging.dto.ChatRoomSummaryDto;
import com.PickOne.domain.messaging.dto.CreateChatRoomRequest;
import com.PickOne.domain.messaging.service.ChatRoomService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.security.model.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatrooms")
@RequiredArgsConstructor
@Tag(name = "채팅방 API", description = "채팅방 생성 및 목록 조회")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "내 채팅방 목록", description = "사용자가 참여한 채팅방 목록과 최근 메시지를 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<BaseResponse<List<ChatRoomSummaryDto>>> getMyRooms(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getUserId();
        return BaseResponse.success(chatRoomService.getChatRoomsWithLatestMessage(userId));
    }

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<ChatRoomDetailDto>> createRoom(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateChatRoomRequest request
    ) {
        Long userId = principal.getUserId();
        ChatRoomDetailDto created = chatRoomService.createRoom(userId, request);
        return BaseResponse.success(created);
    }

    @Operation(summary = "채팅방에 유저 초대", description = "지정한 유저를 기존 채팅방에 초대합니다.")
    @PostMapping("/{roomId}/invite")
    public ResponseEntity<BaseResponse<Void>> inviteUserToRoom(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long roomId,
            @RequestParam Long targetUserId
    ) {
        chatRoomService.inviteUser(roomId, principal.getUserId(), targetUserId);
        return BaseResponse.success();
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<BaseResponse<Void>> deleteRoom(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long roomId
    ) {
        Long userId = principal.getUserId();
        chatRoomService.deleteRoom(roomId, userId);
        return BaseResponse.success();
    }
}
