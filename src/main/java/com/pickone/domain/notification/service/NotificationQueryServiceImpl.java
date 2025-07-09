package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationResponse;
import com.pickone.domain.notification.mapper.NotificationMapper;
import com.pickone.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationQueryServiceImpl implements NotificationQueryService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  @Override
  public List<NotificationResponse> getNotifications(Long userId) {
    var result = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
        .stream()
        .map(notificationMapper::toResponse)
        .toList();

    log.info("[NotificationQueryService] 사용자 알림 목록 조회 - userId: {}, 개수: {}", userId, result.size());
    return result;
  }
}
