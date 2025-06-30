package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import java.util.List;
import java.util.Optional;

public interface ChatRoomUserQueryRepository {
  Optional<ChatRoomUserEntity> findOwnerOfRoom(Long roomId);
  List<Long> findUserIdsByRoomId(Long roomId);
}
