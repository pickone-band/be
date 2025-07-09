package com.pickone.domain.messaging.mapper;

import com.pickone.domain.messaging.document.Message;
import com.pickone.domain.messaging.dto.ChatRoomDetailResponse;
import com.pickone.domain.messaging.dto.ChatRoomSummaryResponse;
import com.pickone.domain.messaging.entity.ChatRoom;
import com.pickone.domain.messaging.entity.ChatRoomUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatRoomMapper {

  public ChatRoomDetailResponse toDetailResponse(ChatRoom room, List<ChatRoomUser> users) {
    List<String> nicknames = users.stream()
        .map(cu -> cu.getUser().getProfile().getNickname())
        .collect(Collectors.toList());

    log.info("[ChatRoomMapper] 채팅방 상세 응답 생성 - roomId: {}", room.getId());
    return new ChatRoomDetailResponse(room.getId(), room.getName(), nicknames);
  }

  public ChatRoomSummaryResponse toSummaryResponse(ChatRoom room, Message lastMessage) {
    return new ChatRoomSummaryResponse(
        room.getId(),
        room.getName(),
        lastMessage != null ? lastMessage.getContent() : null,
        lastMessage != null ? lastMessage.getSentAt() : null
    );
  }
}
