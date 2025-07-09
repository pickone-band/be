package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationResponse;
import com.pickone.domain.notification.entity.Notification;
import com.pickone.domain.notification.mapper.NotificationMapper;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.repository.NotificationRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCommandServiceImpl implements NotificationCommandService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  public NotificationResponse sendNotification(Long userId, String message, NotificationType type) {
    Notification notification = Notification.create(userId, message, type);
    Notification saved = notificationRepository.save(notification);
    log.info("[NotificationCommandService] 알림 전송 완료 - userId: {}, type: {}, message: {}", userId, type, message);
    return notificationMapper.toResponse(saved);
  }

  @Transactional
  @Override
  public void markAsRead(String notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> {
          log.warn("[NotificationCommandService] 알림을 찾을 수 없습니다 - notificationId: {}", notificationId);
          return new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND);
        });

    notification.markRead();
    notificationRepository.save(notification);
    log.info("[NotificationCommandService] 알림 읽음 처리 완료 - notificationId: {}", notificationId);
  }
}
