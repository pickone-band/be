package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.model.document.MessageDocument;
import com.PickOne.domain.messaging.repository.ChatRoomUserRepository;
import com.PickOne.domain.messaging.repository.MessageMongoRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMongoRepository messageMongoRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public MessageDocument sendMessage(Long roomId, Long senderId, String content) {

        if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(senderId, roomId)) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
        return messageMongoRepository.save(
                new MessageDocument(null, roomId, senderId, content, LocalDateTime.now())
        );
    }

    public List<MessageDocument> getMessagesByRoom(Long roomId) {
        return messageMongoRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    public MessageDocument getLastMessage(Long roomId) {
        return messageMongoRepository.findTopByRoomIdOrderBySentAtDesc(roomId);
    }
}
