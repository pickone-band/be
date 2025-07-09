package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.entity.ChatRoomUser;
import java.util.List;
import java.util.Optional;

public interface ChatRoomUserQueryRepository {
  Optional<ChatRoomUser> findOwnerOfRoom(Long roomId);
  List<Long> findUserIdsByRoomId(Long roomId);
}
