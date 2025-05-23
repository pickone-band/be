package com.PickOne.domain.messaging.model.domain;

/**
 * 메시지 상태를 위한 열거형
 */
public enum MessageStatus {
    SENT,       // 발신자에 의해 메시지가 전송됨
    DELIVERED,  // 수신자에게 메시지가 전송됨
    READ        // 수신자에 의해 메시지가 읽힘
}