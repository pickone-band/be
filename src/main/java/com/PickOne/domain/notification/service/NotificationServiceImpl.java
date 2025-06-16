package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.model.entity.NotificationDocument;
import com.PickOne.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationDocument sendNotification(Long recipientId, NotificationType type, String title, String content) {
        NotificationDocument notification = createNotification(recipientId, type, title, content);

        // WebSocket 전송 (예: /topic/notifications/{userId})
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + recipientId,
                notification // 직렬화 가능한 DTO로 변경 가능
        );

        return notification;
    }

    @Override
    public NotificationDocument createNotification(Long recipientId, NotificationType type, String title, String content) {
        NotificationDocument notification = NotificationDocument.builder()
                .recipientId(recipientId)
                .type(type)
                .title(title)
                .content(content)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDocument> getNotifications(Long recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    @Override
    public List<NotificationDocument> getUnreadNotifications(Long recipientId) {
        return notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(
                recipientId, NotificationStatus.UNREAD);
    }

    @Override
    public NotificationDocument markAsRead(String id) {
        NotificationDocument notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다: " + id));

        if (notification.getStatus() == NotificationStatus.UNREAD) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }

        return notification;
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
