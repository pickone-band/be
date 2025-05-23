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
     * 사용자의 모든 알림 찾기
     */
    Page<NotificationDocument> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    /**
     * 사용자의 읽지 않은 알림 찾기
     */
    List<NotificationDocument> findByRecipientIdAndStatusOrderByCreatedAtDesc(Long recipientId, String status);

    /**
     * 사용자의 읽지 않은 알림 수 세기
     */
    long countByRecipientIdAndStatus(Long recipientId, String status);

    /**
     * 사용자의 유형별 알림 찾기
     */
    Page<NotificationDocument> findByRecipientIdAndTypeOrderByCreatedAtDesc(Long recipientId, String type, Pageable pageable);
}