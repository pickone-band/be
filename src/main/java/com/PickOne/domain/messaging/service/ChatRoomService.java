package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.dto.ChatRoomDetailDto;
import com.PickOne.domain.messaging.dto.ChatRoomSummaryDto;
import com.PickOne.domain.messaging.dto.CreateChatRoomRequest;
import com.PickOne.domain.messaging.model.document.MessageDocument;
import com.PickOne.domain.messaging.model.entity.ChatRole;
import com.PickOne.domain.messaging.model.entity.ChatRoomEntity;
import com.PickOne.domain.messaging.model.entity.ChatRoomUserEntity;
import com.PickOne.domain.messaging.repository.ChatRoomRepository;
import com.PickOne.domain.messaging.repository.ChatRoomUserRepository;
import com.PickOne.domain.messaging.repository.MessageAggregationRepository;
import com.PickOne.domain.messaging.repository.MessageMongoRepository;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageMongoRepository messageMongoRepository;
    private final MessageAggregationRepository messageAggregationRepository;
    private final UserJpaRepository userJpaRepository;

    @Transactional
    public ChatRoomDetailDto createRoom(Long creatorId, CreateChatRoomRequest request) {
        ChatRoomEntity room = new ChatRoomEntity(request.name());
        chatRoomRepository.save(room);

        UserEntity creator = userJpaRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        List<UserEntity> participants = userJpaRepository.findAllById(request.participantIds());

        // 참여자 생성
        List<ChatRoomUserEntity> userEntities = new ArrayList<>();
        userEntities.add(new ChatRoomUserEntity(room, creator, ChatRole.OWNER));
        for (UserEntity participant : participants) {
            userEntities.add(new ChatRoomUserEntity(room, participant, ChatRole.MEMBER));
        }

        chatRoomUserRepository.saveAll(userEntities);

        List<String> nicknames = userEntities.stream()
                .map(cu -> cu.getUser().getNickname())
                .toList();

        return new ChatRoomDetailDto(room.getId(), room.getName(), nicknames);
    }

    public ChatRoomEntity findByIdOrThrow(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }

    public List<ChatRoomSummaryDto> getChatRoomsWithLatestMessage(Long userId) {
        // 1. 참여 채팅방 가져오기
        List<ChatRoomUserEntity> participations = chatRoomUserRepository.findByUserId(userId);
        List<Long> roomIds = participations.stream()
                .map(cru -> cru.getChatRoom().getId())
                .toList();

        // 2. 각 채팅방별 마지막 메시지 Mongo에서 한번에 조회 (Aggregation 사용)
        List<MessageDocument> latestMessages = messageAggregationRepository.findLatestMessagesPerRoom(roomIds);

        // 3. roomId → 메시지 매핑
        var messageMap = latestMessages.stream()
                .collect(java.util.stream.Collectors.toMap(MessageDocument::getRoomId, m -> m));

        // 4. 결과 조합
        return participations.stream()
                .map(cru -> {
                    ChatRoomEntity room = cru.getChatRoom();
                    MessageDocument message = messageMap.get(room.getId());

                    return new ChatRoomSummaryDto(
                            room.getId(),
                            room.getName(),
                            message != null ? message.getContent() : null,
                            message != null ? message.getSentAt() : null
                    );
                }).toList();
    }

    @Transactional
    public void inviteUser(Long roomId, Long inviterId, Long targetUserId) {
        ChatRoomEntity room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(inviterId, roomId)) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        UserEntity target = userJpaRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        // 중복 초대 방지
        if (chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)) {
            return;
        }

        ChatRoomUserEntity newEntry = new ChatRoomUserEntity(room, target, ChatRole.MEMBER);
        chatRoomUserRepository.save(newEntry);
    }

    @Transactional
    public void deleteRoom(Long roomId, Long requesterId) {
        ChatRoomEntity room = findByIdOrThrow(roomId);
        ChatRoomUserEntity participation = chatRoomUserRepository
                .findByUserIdAndChatRoomId(requesterId, roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED));

        if (!participation.isOwner()) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN);
        }

        chatRoomUserRepository.deleteAllByChatRoomId(roomId);
        chatRoomRepository.delete(room);
    }

    public void validateUserInRoom(Long roomId, Long userId) {
        if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(userId, roomId)) {
            throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }
    }

}