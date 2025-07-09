package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.entity.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long>, ChatRoomUserQueryRepository {
  List<ChatRoomUser> findByUserId(Long userId);

  Optional<ChatRoomUser> findByUserIdAndChatRoomId(Long userId, Long roomId);

  boolean existsByUserIdAndChatRoomId(Long userId, Long roomId);

  void deleteAllByChatRoomId(Long roomId);
}
