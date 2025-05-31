package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Notification 도메인 객체를 위한 리포지토리 인터페이스
 */
@Repository
public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(String id);
    List<Notification> findByRecipientId(Long recipientId);
    List<Notification> findUnreadByRecipientId(Long recipientId);
    void deleteById(String id);
    void deleteAllByRecipientId(Long recipientId);
}