package com.pickone.domain.messaging.mapper;

import com.pickone.domain.messaging.dto.ChatRoomDetailDto;
import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomDtoMapper {

  public ChatRoomDetailDto toDetailDto(ChatRoomEntity room, List<ChatRoomUserEntity> users) {
    List<String> nicknames = users.stream()
        .map(cu -> cu.getUser().getNickname())
        .toList();
    return new ChatRoomDetailDto(room.getId(), room.getName(), nicknames);
  }
}