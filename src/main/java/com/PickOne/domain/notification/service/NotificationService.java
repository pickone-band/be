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

    /**
     * 사용자를 위한 알림 생성
     */
    Notification createNotification(Long recipientId, NotificationType type, String content,
                                    String refEntityType, Long refEntityId);

    /**
     * 알림을 읽음으로 표시
     */
    Notification markNotificationRead(String notificationId);

    /**
     * 사용자의 모든 알림을 읽음으로 표시
     */
    void markAllNotificationsReadForUser(Long userId);

    /**
     * ID로 알림 가져오기
     */
    Optional<Notification> getNotification(String notificationId);

    /**
     * 사용자의 모든 알림 가져오기
     */
    Page<Notification> getAllNotificationsForUser(Long userId, Pageable pageable);

    /**
     * 사용자의 읽지 않은 알림 가져오기
     */
    List<Notification> getUnreadNotificationsForUser(Long userId);

    /**
     * 사용자의 읽지 않은 알림 수 세기
     */
    long countUnreadNotificationsForUser(Long userId);

    /**
     * 사용자의 유형별 알림 가져오기
     */
    Page<Notification> getNotificationsByTypeForUser(Long userId, NotificationType type, Pageable pageable);
}