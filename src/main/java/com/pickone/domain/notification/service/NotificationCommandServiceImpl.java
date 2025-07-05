package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationDto;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.model.entity.NotificationDocument;
import com.pickone.domain.notification.model.mapper.NotificationMapper;
import com.pickone.domain.notification.repository.NotificationRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

  private final NotificationRepository notificationRepository;

  @Override
  public NotificationDto sendNotification(Long userId, String message, NotificationType type) {
    NotificationDocument doc = NotificationDocument.of(userId, message, type);
    NotificationDocument saved = notificationRepository.save(doc);
    return NotificationMapper.toDto(saved);
  }


  @Transactional
  @Override
  public void markAsRead(String notificationId) {
    NotificationDocument doc = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));
    doc.markRead();
    notificationRepository.save(doc);
  }
}
