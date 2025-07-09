package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationResponse;
import com.pickone.domain.notification.entity.Notification;
import com.pickone.domain.notification.mapper.NotificationMapper;
import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;
import com.pickone.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NotificationQueryServiceImplTest {

  @InjectMocks
  private NotificationQueryServiceImpl notificationQueryService;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private NotificationMapper notificationMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getNotifications_success() {
    // given
    Long userId = 1L;
    Notification notification = mock(Notification.class);
    NotificationResponse response = new NotificationResponse(
        "notif-id",
        userId,
        "새 알림",
        NotificationType.FOLLOW,
        NotificationStatus.UNREAD,
        LocalDateTime.now()
    );

    when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notification));
    when(notificationMapper.toResponse(notification)).thenReturn(response);

    // when
    List<NotificationResponse> results = notificationQueryService.getNotifications(userId);

    // then
    assertEquals(1, results.size());
    assertEquals("notif-id", results.get(0).id());
    verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId);
    verify(notificationMapper).toResponse(notification);
  }
}
