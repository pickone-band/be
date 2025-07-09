package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
