package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.entity.ChatRoomUserEntity;
import com.PickOne.domain.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUserEntity, Long> {

    List<ChatRoomUserEntity> findByUserId(Long userId);

    List<ChatRoomUserEntity> findByChatRoomId(Long roomId);

    Optional<ChatRoomUserEntity> findByUserIdAndChatRoomId(Long userId, Long roomId);

    boolean existsByUserIdAndChatRoomId(Long userId, Long roomId);

    long countByChatRoomId(Long roomId);

    void deleteAllByChatRoomId(Long roomId);
}