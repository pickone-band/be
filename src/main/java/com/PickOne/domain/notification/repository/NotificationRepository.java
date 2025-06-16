package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.entity.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Notification 도메인 객체를 위한 리포지토리 인터페이스
 */
public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {

    List<NotificationDocument> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);

    List<NotificationDocument> findByRecipientIdAndStatusOrderByCreatedAtDesc(
            Long recipientId, NotificationStatus status
    );

    void deleteAllByRecipientId(Long recipientId);
}