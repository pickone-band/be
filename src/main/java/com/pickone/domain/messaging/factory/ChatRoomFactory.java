package com.pickone.domain.messaging.factory;

import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomFactory {
  public ChatRoomEntity create(String name) {
    return new ChatRoomEntity(name);
  }
}
