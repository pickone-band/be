package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.document.MessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {

  List<MessageDocument> findByRoomIdOrderBySentAtAsc(Long roomId);

  List<MessageDocument> findBySenderId(Long senderId);

  MessageDocument findTopByRoomIdOrderBySentAtDesc(Long roomId);
}