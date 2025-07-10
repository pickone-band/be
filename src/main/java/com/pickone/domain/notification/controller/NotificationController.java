package com.pickone.domain.notification.controller;

import com.pickone.domain.notification.dto.NotificationRequest;
import com.pickone.domain.notification.dto.NotificationResponse;
import com.pickone.domain.notification.service.NotificationCommandService;
import com.pickone.domain.notification.service.NotificationQueryService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification", description = "알림 관련 API")
public class NotificationController {

  private final NotificationCommandService commandService;
  private final NotificationQueryService queryService;

  @Operation(summary = "사용자 알림 목록 조회", description = "특정 사용자의 모든 알림을 조회합니다.")
  @GetMapping
  public ResponseEntity<BaseResponse<List<NotificationResponse>>> getUserNotifications(
      @AuthenticationPrincipal(expression = "id") Long userId) {

    log.info("[NotificationController] 사용자 알림 목록 조회 요청 - userId: {}", userId);
    List<NotificationResponse> notifications = queryService.getNotifications(userId);
    return BaseResponse.success(notifications);
  }

  @Operation(summary = "알림 전송", description = "특정 사용자에게 알림을 전송합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<NotificationResponse>> sendNotification(
      @RequestBody NotificationRequest request) {

    log.info("[NotificationController] 알림 전송 요청 - userId: {}, type: {}", request.userId(), request.type());
    NotificationResponse response = commandService.sendNotification(
        request.userId(), request.message(), request.type()
    );
    return BaseResponse.success(response);
  }

  @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 표시합니다.")
  @PatchMapping("/{notificationId}/read")
  public ResponseEntity<BaseResponse<Void>> markAsRead(
      @Parameter(description = "알림 ID", example = "64e83412fa3a") @PathVariable String notificationId) {

    log.info("[NotificationController] 알림 읽음 처리 요청 - notificationId: {}", notificationId);
    commandService.markAsRead(notificationId);
    return BaseResponse.success();
  }
}
