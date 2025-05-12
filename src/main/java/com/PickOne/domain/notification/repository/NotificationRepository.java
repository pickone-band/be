package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Notification domain object
 */
@Repository
public interface NotificationRepository {

    /**
     * Save a notification
     */
    Notification save(Notification notification);

    /**
     * Find a notification by its ID
     */
    Optional<Notification> findById(String id);

    /**
     * Find all notifications for a user
     */
    Page<Notification> findAllForUser(Long userId, Pageable pageable);

    /**
     * Find unread notifications for a user
     */
    List<Notification> findUnreadForUser(Long userId);

    /**
     * Count unread notifications for a user
     */
    long countUnreadForUser(Long userId);

    /**
     * Find notifications by type for a user
     */
    Page<Notification> findByTypeForUser(Long userId, NotificationType type, Pageable pageable);
}
