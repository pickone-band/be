package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.model.entity.NotificationDocument;

import java.util.List;

/**
 * 알림 기능을 위한 서비스 인터페이스
 */
public interface NotificationService {
    NotificationDocument createNotification(Long recipientId, NotificationType type, String title, String content);
    List<NotificationDocument> getNotifications(Long recipientId);
    List<NotificationDocument> getUnreadNotifications(Long recipientId);
    NotificationDocument markAsRead(String id);
    void deleteNotification(String id);
    void deleteAllNotifications(Long recipientId);
    NotificationDocument sendNotification(Long recipientId, NotificationType type, String title, String content);

}