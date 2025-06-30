package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.document.MessageReadStatusDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageReadStatusMongoRepository extends MongoRepository<MessageReadStatusDocument, String> {
  long countByRoomIdAndUserIdAndIsReadFalse(Long roomId, Long userId);
}
