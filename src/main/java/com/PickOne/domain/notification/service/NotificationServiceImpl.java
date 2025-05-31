package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(Long recipientId, NotificationType type, String content, String refEntityType, String refEntityId) {
        Notification notification = Notification.create(recipientId, type, content, refEntityType, refEntityId);
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotifications(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long recipientId) {
        return notificationRepository.findUnreadByRecipientId(recipientId);
    }

    @Override
    public Notification markAsRead(String id) {
        return notificationRepository.findById(id)
                .map(Notification::markAsRead)
                .map(notificationRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + id));
    }

    @Override
    public void deleteNotification(String id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void deleteAllNotifications(Long recipientId) {
        notificationRepository.deleteAllByRecipientId(recipientId);
    }
}
