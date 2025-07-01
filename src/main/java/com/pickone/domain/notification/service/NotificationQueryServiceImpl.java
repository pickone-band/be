package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationDto;
import com.pickone.domain.notification.model.mapper.NotificationMapper;
import com.pickone.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {
  private final NotificationRepository notificationRepository;

  @Override
  public List<NotificationDto> getNotifications(Long userId) {
    return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
        .stream().map(NotificationMapper::toDto).toList();
  }
}
