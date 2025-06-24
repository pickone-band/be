package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

}
