package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUserEntity, Long>, ChatRoomUserQueryRepository {
  List<ChatRoomUserEntity> findByUserId(Long userId);

  Optional<ChatRoomUserEntity> findByUserIdAndChatRoomId(Long userId, Long roomId);

  boolean existsByUserIdAndChatRoomId(Long userId, Long roomId);

  void deleteAllByChatRoomId(Long roomId);
}
