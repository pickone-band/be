package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.dto.ChatRoomSummaryDto;
import com.pickone.domain.messaging.dto.CreateChatRoomRequest;
import com.pickone.domain.messaging.model.document.MessageDocument;
import com.pickone.domain.messaging.model.entity.ChatRole;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.repository.ChatRoomRepository;
import com.pickone.domain.messaging.repository.ChatRoomUserRepository;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.messaging.repository.MessageMongoRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;
  private final ChatRoomUserRepository chatRoomUserRepository;
  private final MessageMongoRepository messageMongoRepository;
  private final MessageAggregationRepository messageAggregationRepository;
  private final UserJpaRepository userJpaRepository;

  @Transactional
  public ChatRoomDetailDto createRoom(Long creatorId, CreateChatRoomRequest request) {
    log.info("채팅방 생성 요청: creatorId={}, name={}, participants={}", creatorId, request.name(),
        request.participantIds());

    ChatRoomEntity room = new ChatRoomEntity(request.name());
    chatRoomRepository.save(room);

    UserEntity creator = userJpaRepository.findById(creatorId)
        .orElseThrow(() -> {
          log.warn("채팅방 생성 실패 - 생성자 없음: id={}", creatorId);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });

    List<UserEntity> participants = userJpaRepository.findAllById(request.participantIds());

    List<ChatRoomUserEntity> userEntities = new ArrayList<>();
    userEntities.add(new ChatRoomUserEntity(room, creator, ChatRole.OWNER));
    for (UserEntity participant : participants) {
      userEntities.add(new ChatRoomUserEntity(room, participant, ChatRole.MEMBER));
    }

    chatRoomUserRepository.saveAll(userEntities);

    log.info("채팅방 생성 완료: roomId={}, participants={}", room.getId(), userEntities.size());

    List<String> nicknames = userEntities.stream()
        .map(cu -> cu.getUser().getNickname())
        .toList();

    return new ChatRoomDetailDto(room.getId(), room.getName(), nicknames);
  }

  public ChatRoomEntity findByIdOrThrow(Long roomId) {
    return chatRoomRepository.findById(roomId)
        .orElseThrow(() -> {
          log.warn("채팅방 조회 실패: roomId={}", roomId);
          return new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND);
        });
  }

  public List<ChatRoomSummaryDto> getChatRoomsWithLatestMessage(Long userId) {
    log.info("채팅방 목록 + 최신 메시지 조회 요청: userId={}", userId);

    List<ChatRoomUserEntity> participations = chatRoomUserRepository.findByUserId(userId);
    List<Long> roomIds = participations.stream()
        .map(cru -> cru.getChatRoom().getId())
        .toList();

    List<MessageDocument> latestMessages = messageAggregationRepository.findLatestMessagesPerRoom(
        roomIds);

    var messageMap = latestMessages.stream()
        .collect(java.util.stream.Collectors.toMap(MessageDocument::getRoomId, m -> m));

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
    log.info("채팅방 초대 요청: roomId={}, inviterId={}, targetUserId={}", roomId, inviterId,
        targetUserId);

    ChatRoomEntity room = findByIdOrThrow(roomId);

    if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(inviterId, roomId)) {
      log.warn("채팅방 초대 거부 - 초대 권한 없음: inviterId={}, roomId={}", inviterId, roomId);
      throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }

    UserEntity target = userJpaRepository.findById(targetUserId)
        .orElseThrow(() -> {
          log.warn("초대 대상자 없음: targetUserId={}", targetUserId);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });

    if (chatRoomUserRepository.existsByUserIdAndChatRoomId(targetUserId, roomId)) {
      log.info("이미 참여 중인 사용자: roomId={}, targetUserId={}", roomId, targetUserId);
      return;
    }

    chatRoomUserRepository.save(new ChatRoomUserEntity(room, target, ChatRole.MEMBER));
    log.info("초대 완료: roomId={}, userId={}", roomId, targetUserId);
  }

  @Transactional
  public void deleteRoom(Long roomId, Long requesterId) {
    log.info("채팅방 삭제 요청: roomId={}, requesterId={}", roomId, requesterId);

    ChatRoomEntity room = findByIdOrThrow(roomId);
    ChatRoomUserEntity participation = chatRoomUserRepository
        .findByUserIdAndChatRoomId(requesterId, roomId)
        .orElseThrow(() -> {
          log.warn("채팅방 삭제 실패 - 참여자 아님: userId={}, roomId={}", requesterId, roomId);
          return new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        });

    if (!participation.isOwner()) {
      log.warn("채팅방 삭제 거부 - OWNER 아님: userId={}, roomId={}", requesterId, roomId);
      throw new BusinessException(ErrorCode.CHAT_ROOM_DELETE_FORBIDDEN);
    }

    chatRoomUserRepository.deleteAllByChatRoomId(roomId);
    chatRoomRepository.delete(room);
    log.info("채팅방 삭제 완료: roomId={}", roomId);
  }

  public void validateUserInRoom(Long roomId, Long userId) {
    if (!chatRoomUserRepository.existsByUserIdAndChatRoomId(userId, roomId)) {
      log.warn("채팅방 접근 거부: userId={}, roomId={}", userId, roomId);
      throw new BusinessException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
    }
  }
}
