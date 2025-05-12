package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.entity.NotificationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMongoRepository extends MongoRepository<NotificationDocument, String> {

    /**
     * Find all notifications for a user
     */
    Page<NotificationDocument> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    /**
     * Find unread notifications for a user
     */
    List<NotificationDocument> findByRecipientIdAndStatusOrderByCreatedAtDesc(Long recipientId, String status);

    /**
     * Count unread notifications for a user
     */
    long countByRecipientIdAndStatus(Long recipientId, String status);

    /**
     * Find notifications by type for a user
     */
    Page<NotificationDocument> findByRecipientIdAndTypeOrderByCreatedAtDesc(Long recipientId, String type, Pageable pageable);
}
