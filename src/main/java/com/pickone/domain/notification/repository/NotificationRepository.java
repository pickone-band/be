package com.pickone.domain.notification.repository;

import com.pickone.domain.notification.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
  List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
