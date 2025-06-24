package com.pickone.domain.notification.service;

import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.model.entity.NotificationDocument;
import com.pickone.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public NotificationDocument sendNotification(Long recipientId, NotificationType type,
      String title, String content) {
    log.info("알림 생성 및 전송: recipientId={}, type={}, title={}", recipientId, type, title);
    NotificationDocument notification = createNotification(recipientId, type, title, content);

    messagingTemplate.convertAndSend(
        "/topic/notifications/" + recipientId,
        notification
    );

    log.info("WebSocket 알림 전송 완료: notificationId={}, recipientId={}", notification.getId(),
        recipientId);
    return notification;
  }

  @Override
  public NotificationDocument createNotification(Long recipientId, NotificationType type,
      String title, String content) {
    log.info("알림 생성 요청: recipientId={}, type={}, title={}", recipientId, type, title);
    NotificationDocument notification = NotificationDocument.builder()
        .recipientId(recipientId)
        .type(type)
        .title(title)
        .content(content)
        .status(NotificationStatus.UNREAD)
        .createdAt(LocalDateTime.now())
        .build();
    NotificationDocument saved = notificationRepository.save(notification);
    log.info("알림 저장 완료: notificationId={}", saved.getId());
    return saved;
  }

  @Override
  public List<NotificationDocument> getNotifications(Long recipientId) {
    log.info("전체 알림 조회: recipientId={}", recipientId);
    return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
  }

  @Override
  public List<NotificationDocument> getUnreadNotifications(Long recipientId) {
    log.info("읽지 않은 알림 조회: recipientId={}", recipientId);
    return notificationRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(
        recipientId, NotificationStatus.UNREAD);
  }

  @Override
  public NotificationDocument markAsRead(String id) {
    log.info("알림 읽음 처리 시도: id={}", id);
    NotificationDocument notification = notificationRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("알림 없음 - 읽음 처리 실패: id={}", id);
          return new IllegalArgumentException("알림을 찾을 수 없습니다: " + id);
        });

    if (notification.getStatus() == NotificationStatus.UNREAD) {
      notification.setStatus(NotificationStatus.READ);
      notification.setReadAt(LocalDateTime.now());
      NotificationDocument updated = notificationRepository.save(notification);
      log.info("알림 읽음 처리 완료: id={}", id);
      return updated;
    }

    log.info("이미 읽은 알림: id={}", id);
    return notification;
  }

  @Override
  public void deleteNotification(String id) {
    log.info("알림 삭제 요청: id={}", id);
    notificationRepository.deleteById(id);
  }

  @Override
  public void deleteAllNotifications(Long recipientId) {
    log.info("사용자 알림 전체 삭제: recipientId={}", recipientId);
    notificationRepository.deleteAllByRecipientId(recipientId);
  }
}
