package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for notification functionality
 */
public interface NotificationService {

    /**
     * Create a notification for a user
     */
    Notification createNotification(Long recipientId, NotificationType type, String content,
                                    String refEntityType, Long refEntityId);

    /**
     * Mark a notification as read
     */
    Notification markNotificationRead(String notificationId);

    /**
     * Mark all notifications as read for a user
     */
    void markAllNotificationsReadForUser(Long userId);

    /**
     * Get a notification by its ID
     */
    Optional<Notification> getNotification(String notificationId);

    /**
     * Get all notifications for a user
     */
    Page<Notification> getAllNotificationsForUser(Long userId, Pageable pageable);

    /**
     * Get unread notifications for a user
     */
    List<Notification> getUnreadNotificationsForUser(Long userId);

    /**
     * Count unread notifications for a user
     */
    long countUnreadNotificationsForUser(Long userId);

    /**
     * Get notifications by type for a user
     */
    Page<Notification> getNotificationsByTypeForUser(Long userId, NotificationType type, Pageable pageable);
}