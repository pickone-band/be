package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationResponse;
import com.pickone.domain.notification.entity.Notification;
import com.pickone.domain.notification.mapper.NotificationMapper;
import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.repository.NotificationRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationCommandServiceImplTest {

  @InjectMocks
  private NotificationCommandServiceImpl notificationCommandService;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private NotificationMapper notificationMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void sendNotification_success() {
    // given
    Long userId = 1L;
    String message = "지원 결과가 도착했습니다.";
    NotificationType type = NotificationType.APPLICATION;

    Notification notification = mock(Notification.class);
    Notification saved = mock(Notification.class);
    NotificationResponse response = new NotificationResponse(
        "64f1123a34e6d52267c12c8b",
        userId,
        message,
        type,
        NotificationStatus.UNREAD,
        LocalDateTime.now()
    );

    when(notificationRepository.save(any())).thenReturn(saved);
    when(notificationMapper.toResponse(saved)).thenReturn(response);

    // when
    NotificationResponse result = notificationCommandService.sendNotification(userId, message, type);

    // then
    assertEquals(userId, result.userId());
    assertEquals(message, result.message());
    assertEquals(type, result.type());
    verify(notificationRepository).save(any());
    verify(notificationMapper).toResponse(saved);
  }

  @Test
  void markAsRead_success() {
    // given
    String notificationId = "64f1123a34e6d52267c12c8b";
    Notification notification = mock(Notification.class);

    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

    // when
    notificationCommandService.markAsRead(notificationId);

    // then
    verify(notificationRepository).findById(notificationId);
    verify(notification).markRead();
    verify(notificationRepository).save(notification);
  }

  @Test
  void markAsRead_fail_notificationNotFound() {
    // given
    String notificationId = "invalidId";
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class,
        () -> notificationCommandService.markAsRead(notificationId));

    assertEquals(ErrorCode.NOTIFICATION_NOT_FOUND, ex.getErrorCode());
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository, never()).save(any());
  }

  @Test
  void getNotifications_success() {
    // given
    Long userId = 1L;
    Notification notification = mock(Notification.class);
    NotificationResponse response = new NotificationResponse(
        "notif-id",
        userId,
        "알림 메시지",
        NotificationType.APPLICATION,
        NotificationStatus.UNREAD,
        LocalDateTime.now()
    );

    when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notification));
    when(notificationMapper.toResponse(notification)).thenReturn(response);

    NotificationQueryServiceImpl queryService = new NotificationQueryServiceImpl(notificationRepository, notificationMapper);

    // when
    List<NotificationResponse> results = queryService.getNotifications(userId);

    // then
    assertEquals(1, results.size());
    assertEquals("notif-id", results.get(0).id());
    verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId);
    verify(notificationMapper).toResponse(notification);
  }
}
