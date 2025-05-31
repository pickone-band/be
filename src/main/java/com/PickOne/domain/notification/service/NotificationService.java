package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 알림 기능을 위한 서비스 인터페이스
 */
public interface NotificationService {
    Notification createNotification(Long recipientId, NotificationType type, String content, String refEntityType, String refEntityId);
    List<Notification> getNotifications(Long recipientId);
    List<Notification> getUnreadNotifications(Long recipientId);
    Notification markAsRead(String id);
    void deleteNotification(String id);
    void deleteAllNotifications(Long recipientId);
}