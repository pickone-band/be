package com.pickone.domain.notification.dto;

import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "알림 응답")
public record NotificationResponse(
    @Schema(description = "알림 ID", example = "64f1123a34e6d52267c12c8b") String id,
    @Schema(description = "사용자 ID", example = "42") Long userId,
    @Schema(description = "알림 메시지", example = "지원 결과가 도착했습니다.") String message,
    @Schema(description = "알림 유형", example = "APPLICATION") NotificationType type,
    @Schema(description = "읽음 여부", example = "UNREAD") NotificationStatus status,
    @Schema(description = "생성 시각", example = "2024-07-08T21:35:10") LocalDateTime createdAt
) {}
