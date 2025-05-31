package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.entity.NotificationDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface NotificationMongoRepository extends MongoRepository<NotificationDocument, String> {

    List<NotificationDocument> findByRecipientId(Long recipientId);
    List<NotificationDocument> findByRecipientIdAndStatus(Long recipientId, String status);
    void deleteByRecipientId(Long recipientId);
}