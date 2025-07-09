package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.document.Message;
import com.pickone.domain.messaging.dto.ChatRoomDetailResponse;
import com.pickone.domain.messaging.dto.ChatRoomSummaryResponse;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.entity.ChatRole;
import com.pickone.domain.messaging.entity.ChatRoom;
import com.pickone.domain.messaging.entity.ChatRoomUser;
import com.pickone.domain.messaging.mapper.ChatRoomMapper;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;
  private final UserJpaRepository userJpaRepository;
  private final MessageAggregationRepository messageAggregationRepository;
  private final ChatRoomMapper chatRoomMapper;

  public ChatRoomDetailResponse createRoom(Long creatorId, CreateChatRoomRequest request) {
    User creator = userJpaRepository.findById(creatorId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    List<User> participants = userJpaRepository.findAllById(request.participantIds());

    ChatRoom room = ChatRoom.create(request.name());
    chatRoomRepository.save(room);

    List<ChatRoomUser> userEntities = new ArrayList<>();
    userEntities.add(ChatRoomUser.create(room, creator, ChatRole.OWNER));

    for (User participant : participants) {
      userEntities.add(ChatRoomUser.create(room, participant, ChatRole.MEMBER));
    }

    chatRoomUserRepository.saveAll(userEntities);

    log.info("[ChatRoomService] 채팅방 생성 완료 - roomId: {}, name: {}", room.getId(), room.getName());
    return chatRoomMapper.toDetailResponse(room, userEntities);
  }

  public List<ChatRoomSummaryResponse> getChatRoomsWithLatestMessage(Long userId) {
    List<ChatRoomUser> participations = chatRoomUserRepository.findByUserId(userId);
    List<Long> roomIds = participations.stream()
        .map(cu -> cu.getChatRoom().getId())
        .toList();

    List<Message> latestMessages = messageAggregationRepository.findLatestMessagesPerRoom(roomIds);
    Map<Long, Message> messageMap = latestMessages.stream()
        .collect(Collectors.toMap(Message::getRoomId, m -> m));

    log.info("[ChatRoomService] 채팅방 목록 조회 - userId: {}, 참여 채팅방 수: {}", userId, participations.size());

    return participations.stream()
        .map(cu -> {
          ChatRoom room = cu.getChatRoom();
          Message msg = messageMap.get(room.getId());
          return new ChatRoomSummaryResponse(
              room.getId(),
              room.getName(),
              msg != null ? msg.getContent() : null,
              msg != null ? msg.getSentAt() : null
          );
        }).toList();
  }
}