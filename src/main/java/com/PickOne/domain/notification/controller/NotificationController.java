package com.PickOne.domain.notification.controller;

import com.PickOne.domain.notification.dto.NotificationDto;
import com.PickOne.domain.notification.mapper.NotificationMapper;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 API", description = "실시간 알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 생성", description = "알림을 수동으로 생성합니다.")
    @PostMapping("/send")
    public ResponseEntity<NotificationDto> sendNotification(
            @RequestParam Long recipientId,
            @RequestParam NotificationType type,
            @RequestParam String content,
            @RequestParam String refEntityType,
            @RequestParam String refEntityId
    ) {
        Notification notification = notificationService.createNotification(recipientId, type, content, refEntityType, refEntityId);
        return ResponseEntity.ok(NotificationMapper.toDto(notification));
    }

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@RequestParam Long recipientId) {
        List<NotificationDto> notifications = notificationService.getNotifications(recipientId).stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    @Operation(summary = "읽지 않은 알림 조회", description = "사용자의 읽지 않은 알림을 조회합니다.")
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDto>> getUnread(@RequestParam Long recipientId) {
        List<NotificationDto> unread = notificationService.getUnreadNotifications(recipientId).stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(unread);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @PostMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable String id) {
        Notification updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(NotificationMapper.toDto(updated));
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 알림 전체 삭제", description = "특정 사용자의 모든 알림을 삭제합니다.")
    @DeleteMapping("/user/{recipientId}")
    public ResponseEntity<Void> deleteAll(@PathVariable Long recipientId) {
        notificationService.deleteAllNotifications(recipientId);
        return ResponseEntity.noContent().build();
    }
}