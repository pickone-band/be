package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.document.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageMongoRepository extends MongoRepository<Message, String> {
  List<Message> findByRoomId(Long roomId);
}
