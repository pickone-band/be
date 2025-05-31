package com.PickOne.domain.messaging.mapper;


import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.model.entity.MessageDocument;

public class MessageMapper {

    public static Message toDomain(MessageDocument doc) {
        return new Message(
                doc.getId(),
                doc.getSenderId(),
                doc.getRecipientId(),
                doc.getContent(),
                MessageStatus.valueOf(doc.getStatus()),
                doc.getSentAt(),
                doc.getDeliveredAt(),
                doc.getReadAt()
        );
    }

    public static MessageDocument toDocument(Message message) {
        return new MessageDocument(
                message.getId(),
                message.getSenderId(),
                message.getRecipientId(),
                message.getContent(),
                message.getStatus().name(),
                message.getSentAt(),
                message.getDeliveredAt(),
                message.getReadAt()
        );
    }

    public static MessageDto toDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSenderId(),
                message.getRecipientId(),
                message.getContent(),
                message.getStatus().name(),
                message.getSentAt(),
                message.getDeliveredAt(),
                message.getReadAt()
        );
    }
}
