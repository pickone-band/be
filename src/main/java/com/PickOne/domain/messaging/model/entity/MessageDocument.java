package com.PickOne.domain.messaging.model.entity;

import com.PickOne.domain.messaging.model.domain.Message;

import com.PickOne.domain.messaging.model.domain.MessageStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * 메시지 저장을 위한 MongoDB 문서
 */
@Document(collection = "messages")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MessageDocument {

    @Id
    private String id;
    private Long senderId;
    private Long recipientId;
    private String content;
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
}
