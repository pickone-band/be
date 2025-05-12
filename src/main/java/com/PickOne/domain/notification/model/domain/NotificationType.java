package com.PickOne.domain.notification.model.domain;

/**
 * Enum for Notification Type
 */
public enum NotificationType {
    NEW_MESSAGE("새 메시지가 도착했습니다"),
    RECRUITMENT_APPLICATION("새 모집 신청이 있습니다"),
    RECRUITMENT_ACCEPTED("모집 신청이 수락되었습니다"),
    RECRUITMENT_REJECTED("모집 신청이 거절되었습니다"),
    SYSTEM_ANNOUNCEMENT("시스템 공지사항");

    private final String defaultMessage;

    NotificationType(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}