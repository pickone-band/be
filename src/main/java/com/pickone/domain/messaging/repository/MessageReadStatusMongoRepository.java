package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.document.MessageReadStatusDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MessageReadStatusMongoRepository extends
    MongoRepository<MessageReadStatusDocument, String> {

  List<MessageReadStatusDocument> findByMessageId(String messageId);

  Optional<MessageReadStatusDocument> findByMessageIdAndUserId(String messageId, Long userId);

  List<MessageReadStatusDocument> findByUserId(Long userId);
}
