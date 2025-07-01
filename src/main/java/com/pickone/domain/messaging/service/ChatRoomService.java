package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.factory.ChatRoomFactory;
import com.pickone.domain.messaging.mapper.ChatRoomDtoMapper;
import com.pickone.domain.messaging.model.entity.ChatRole;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;
  private final UserJpaRepository userJpaRepository;
  private final ChatRoomFactory chatRoomFactory;
  private final ChatRoomDtoMapper chatRoomDtoMapper;
  private final MessageAggregationRepository messageAggregationRepository;

  public ChatRoomDetailDto createRoom(Long creatorId, CreateChatRoomRequest request) {
    UserEntity creator = userJpaRepository.findById(creatorId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    List<UserEntity> participants = userJpaRepository.findAllById(request.participantIds());

    ChatRoomEntity room = chatRoomFactory.create(request.name());
    chatRoomRepository.save(room);

    List<ChatRoomUserEntity> userEntities = new ArrayList<>();
    userEntities.add(new ChatRoomUserEntity(room, creator, ChatRole.OWNER));
    for (UserEntity participant : participants) {
      userEntities.add(new ChatRoomUserEntity(room, participant, ChatRole.MEMBER));
    }
    chatRoomUserRepository.saveAll(userEntities);

    return chatRoomDtoMapper.toDetailDto(room, userEntities);
  }

  public ChatRoomEntity findByIdOrThrow(Long roomId) {
    return chatRoomRepository.findById(roomId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));
  }

  public List<ChatRoomSummaryDto> getChatRoomsWithLatestMessage(Long userId) {
    List<ChatRoomUserEntity> participations = chatRoomUserRepository.findByUserId(userId);
    List<Long> roomIds = participations.stream()
        .map(cu -> cu.getChatRoom().getId())
        .toList();

    List<MessageDocument> latestMessages =
        messageAggregationRepository.findLatestMessagesPerRoom(roomIds);

    Map<Long, MessageDocument> messageMap = latestMessages.stream()
        .collect(Collectors.toMap(MessageDocument::getRoomId, m -> m));

    return participations.stream()
        .map(cu -> {
          ChatRoomEntity room = cu.getChatRoom();
          MessageDocument msg = messageMap.get(room.getId());
          return new ChatRoomSummaryDto(
              room.getId(),
              room.getName(),
              msg != null ? msg.getContent() : null,
              msg != null ? msg.getSentAt() : null
          );
        }).toList();
  }
}
