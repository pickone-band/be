package com.pickone.domain.notification.repository;

import com.pickone.domain.notification.model.entity.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {
  List<NotificationDocument> findByUserIdOrderByCreatedAtDesc(Long userId);
}
