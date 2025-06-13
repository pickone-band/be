package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.model.entity.ChatRoomUserEntity;
import com.PickOne.domain.messaging.repository.ChatRoomUserRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomUserService {

    private final ChatRoomUserRepository chatRoomUserRepository;

    public boolean isUserInRoom(Long userId, Long roomId) {
        return chatRoomUserRepository.existsByUserIdAndChatRoomId(userId, roomId);
    }

    public ChatRoomUserEntity getParticipation(Long userId, Long roomId) {
        return chatRoomUserRepository.findByUserIdAndChatRoomId(userId, roomId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED));
    }

    public long countParticipants(Long roomId) {
        return chatRoomUserRepository.countByChatRoomId(roomId);
    }

    public List<ChatRoomUserEntity> getParticipants(Long roomId) {
        return chatRoomUserRepository.findByChatRoomId(roomId);
    }
}