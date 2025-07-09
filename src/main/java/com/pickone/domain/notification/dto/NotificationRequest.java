package com.pickone.domain.notification.dto;

import com.pickone.domain.notification.model.domain.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "알림 생성 요청")
public record NotificationRequest(
    @Schema(description = "사용자 ID", example = "42") Long userId,
    @Schema(description = "알림 메시지", example = "새로운 지원이 도착했습니다.") String message,
    @Schema(description = "알림 유형", example = "APPLICATION") NotificationType type
) {}
