package com.PickOne.domain.messaging.dto;

import com.PickOne.domain.messaging.model.domain.Message;
import java.time.LocalDateTime;

/**
 * 메시지 데이터 전송 객체 (DTO)
 * record를 사용하여 불변성 보장
 */
public record MessageDto(
        String id,
        Long senderId,
        Long recipientId,
        String content,
        String status,
        LocalDateTime sentAt,
        LocalDateTime deliveredAt,
        LocalDateTime readAt
) {
    /**
     * 도메인 객체로부터 DTO 생성
     */
    public static MessageDto fromDomain(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSenderIdValue(),
                message.getRecipientIdValue(),
                message.getContentValue(),
                message.getStatus().name(),
                message.getSentAt(),
                message.getDeliveredAt(),
                message.getReadAt()
        );
    }
}