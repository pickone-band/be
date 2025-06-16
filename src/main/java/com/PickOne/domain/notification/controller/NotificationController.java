package com.PickOne.domain.notification.controller;

import com.PickOne.domain.notification.dto.NotificationDto;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.model.entity.NotificationDocument;
import com.PickOne.domain.notification.service.NotificationService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 API", description = "실시간 알림 관련 API")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send")
    @Operation(summary = "알림 전송", description = "알림을 수동으로 생성하고 전송합니다.")
    public ResponseEntity<BaseResponse<NotificationDto>> sendNotification(
            @RequestParam Long recipientId,
            @RequestParam NotificationType type,
            @RequestParam String title,
            @RequestParam String content
    ) {
        NotificationDocument doc = notificationService.sendNotification(recipientId, type, title, content);
        NotificationDto dto = new NotificationDto(
                doc.getId(), doc.getRecipientId(), doc.getType(),
                doc.getTitle(), doc.getContent(), doc.getStatus(),
                doc.getCreatedAt(), doc.getReadAt()
        );
        return BaseResponse.success(SuccessCode.CREATED, dto);
    }

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다.")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getNotifications(@RequestParam Long recipientId) {
        List<NotificationDto> list = notificationService.getNotifications(recipientId)
                .stream()
                .map(doc -> new NotificationDto(
                        doc.getId(), doc.getRecipientId(), doc.getType(),
                        doc.getTitle(), doc.getContent(), doc.getStatus(),
                        doc.getCreatedAt(), doc.getReadAt()))
                .toList();

        return BaseResponse.success(SuccessCode.OK, list);
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 조회", description = "사용자의 읽지 않은 알림을 조회합니다.")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getUnread(@RequestParam Long recipientId) {
        List<NotificationDto> unread = notificationService.getUnreadNotifications(recipientId)
                .stream()
                .map(doc -> new NotificationDto(
                        doc.getId(), doc.getRecipientId(), doc.getType(),
                        doc.getTitle(), doc.getContent(), doc.getStatus(),
                        doc.getCreatedAt(), doc.getReadAt()))
                .toList();

        return BaseResponse.success(SuccessCode.OK, unread);
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    public ResponseEntity<BaseResponse<NotificationDto>> markAsRead(@PathVariable String id) {
        NotificationDocument doc = notificationService.markAsRead(id);
        NotificationDto dto = new NotificationDto(
                doc.getId(), doc.getRecipientId(), doc.getType(),
                doc.getTitle(), doc.getContent(), doc.getStatus(),
                doc.getCreatedAt(), doc.getReadAt()
        );
        return BaseResponse.success(SuccessCode.UPDATED, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return BaseResponse.success(SuccessCode.DELETED);
    }

    @DeleteMapping("/user/{recipientId}")
    @Operation(summary = "사용자 알림 전체 삭제", description = "특정 사용자의 모든 알림을 삭제합니다.")
    public ResponseEntity<BaseResponse<Void>> deleteAll(@PathVariable Long recipientId) {
        notificationService.deleteAllNotifications(recipientId);
        return BaseResponse.success(SuccessCode.DELETED);
    }
}
