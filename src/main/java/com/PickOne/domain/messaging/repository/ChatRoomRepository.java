package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
}
