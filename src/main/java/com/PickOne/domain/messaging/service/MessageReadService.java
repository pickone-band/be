package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.dto.ReadCountDto;
import com.PickOne.domain.messaging.model.document.MessageReadStatusDocument;
import com.PickOne.domain.messaging.repository.ChatRoomUserRepository;
import com.PickOne.domain.messaging.repository.MessageReadStatusMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageReadService {

    private final MessageReadStatusMongoRepository readRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    public void markAsRead(String messageId, Long userId) {
        readRepository.findByMessageIdAndUserId(messageId, userId).ifPresentOrElse(
                existing -> {}, // 이미 읽었으면 무시
                () -> readRepository.save(new MessageReadStatusDocument(
                        null, messageId, userId, LocalDateTime.now()
                ))
        );
    }

    public long getReadCount(String messageId) {
        return readRepository.findByMessageId(messageId).size();
    }

    public long getTotalParticipantCount(Long roomId) {
        return chatRoomUserRepository.countByChatRoomId(roomId);
    }

    public ReadCountDto getReadStatus(String messageId, Long roomId) {
        long total = getTotalParticipantCount(roomId);
        long read = getReadCount(messageId);
        return new ReadCountDto(messageId, read, total);
    }
}
