package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.model.document.MessageDocument;
import com.PickOne.domain.messaging.repository.ChatRoomUserRepository;
import com.PickOne.domain.messaging.repository.MessageMongoRepository;
import com.PickOne.domain.notification.model.domain.MessageSentEvent;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMongoRepository messageMongoRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ApplicationEventPublisher eventPublisher; // 추가

    public MessageDocument sendMessage(Long roomId, Long senderId, String content) {

        if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        MessageDocument message = messageMongoRepository.save(
                new MessageDocument(null, roomId, senderId, content, LocalDateTime.now())
        );

        // 채팅방 참여자 중 발신자 제외한 수신자 전체 조회
        List<Long> recipientIds = chatRoomUserRepository.findByChatRoomId(roomId).stream()
                .map(chatRoomUser -> chatRoomUser.getUser().getId())
                .filter(id -> !id.equals(senderId)) // sender 제외
                .toList();

        eventPublisher.publishEvent(new MessageSentEvent(senderId, recipientIds, content));

        return message;
    }

    public List<MessageDocument> getMessagesByRoom(Long roomId) {
        return messageMongoRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    public MessageDocument getLastMessage(Long roomId) {
        return messageMongoRepository.findTopByRoomIdOrderBySentAtDesc(roomId);
    }
}
