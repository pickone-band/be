package com.PickOne.domain.notification.controller;

import com.PickOne.domain.notification.dto.NotificationDto;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.service.NotificationService;
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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 API", description = "실시간 알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 현재 사용자의 모든 알림을 조회하는 REST 엔드포인트
     */
    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<NotificationDto>>> getAllNotifications(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long currentUserId = getCurrentUserId();
        Page<Notification> notifications = notificationService.getAllNotificationsForUser(currentUserId, pageable);

        Page<NotificationDto> notificationDtos = notifications.map(NotificationDto::fromDomain);
        return BaseResponse.success(notificationDtos);
    }

    /**
     * 현재 사용자의 읽지 않은 알림을 조회하는 REST 엔드포인트
     */
    @Operation(summary = "읽지 않은 알림 목록 조회", description = "읽지 않은 알림 목록을 조회합니다.")
    @GetMapping("/unread")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getUnreadNotifications() {
        Long currentUserId = getCurrentUserId();
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(currentUserId);

        List<NotificationDto> notificationDtos = unreadNotifications.stream()
                .map(NotificationDto::fromDomain)
                .collect(Collectors.toList());

        return BaseResponse.success(notificationDtos);
    }

    /**
     * 읽지 않은 알림 수를 조회하는 REST 엔드포인트
     */
    @Operation(summary = "읽지 않은 알림 수 조회", description = "읽지 않은 알림의 수를 조회합니다.")
    @GetMapping("/unread/count")
    public ResponseEntity<BaseResponse<Long>> getUnreadNotificationCount() {
        Long currentUserId = getCurrentUserId();
        long count = notificationService.countUnreadNotificationsForUser(currentUserId);

        return BaseResponse.success(count);
    }

    /**
     * 알림을 읽음 상태로 표시하는 REST 엔드포인트
     */
    @Operation(summary = "알림 읽음 표시", description = "알림을 읽음 상태로 표시합니다.")
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<BaseResponse<NotificationDto>> markNotificationRead(
            @PathVariable String notificationId) {

        Notification notification = notificationService.markNotificationRead(notificationId);
        return BaseResponse.success(NotificationDto.fromDomain(notification));
    }

    /**
     * 모든 알림을 읽음 상태로 표시하는 REST 엔드포인트
     */
    @Operation(summary = "모든 알림 읽음 표시", description = "모든 알림을 읽음 상태로 표시합니다.")
    @PostMapping("/read-all")
    public ResponseEntity<BaseResponse<Void>> markAllNotificationsRead() {
        Long currentUserId = getCurrentUserId();
        notificationService.markAllNotificationsReadForUser(currentUserId);

        return BaseResponse.success();
    }

    /**
     * 유형별 알림을 조회하는 REST 엔드포인트
     */
    @Operation(summary = "유형별 알림 목록 조회", description = "특정 유형의 알림 목록을 조회합니다.")
    @GetMapping("/type/{type}")
    public ResponseEntity<BaseResponse<Page<NotificationDto>>> getNotificationsByType(
            @PathVariable String type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Long currentUserId = getCurrentUserId();
        NotificationType notificationType = NotificationType.valueOf(type);

        Page<Notification> notifications = notificationService.getNotificationsByTypeForUser(
                currentUserId, notificationType, pageable);

        Page<NotificationDto> notificationDtos = notifications.map(NotificationDto::fromDomain);
        return BaseResponse.success(notificationDtos);
    }

    /**
     * 알림을 읽음으로 표시하는 WebSocket 엔드포인트
     */
    @MessageMapping("/notification.read")
    public void handleNotificationRead(@Payload String notificationId) {
        notificationService.markNotificationRead(notificationId);
    }

    /**
     * 현재 인증된 사용자의 ID를 가져옴
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName());
    }
}