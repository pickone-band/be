package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.document.MessageReadStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageReadStatusMongoRepository extends MongoRepository<MessageReadStatus, String> {
  long countByRoomIdAndUserIdAndIsReadFalse(Long roomId, Long userId);
}
