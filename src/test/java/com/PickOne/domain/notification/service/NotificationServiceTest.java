package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        notificationService = new NotificationServiceImpl(notificationRepository);
    }

    @Test
    @DisplayName("알림 생성")
    void createNotification() {
        Notification created = Notification.create(1L, NotificationType.NEW_MESSAGE, NotificationType.NEW_MESSAGE.getDefaultMessage(), "MESSAGE", "100L");
        when(notificationRepository.save(any())).thenReturn(created);

        Notification result = notificationService.createNotification(1L, NotificationType.NEW_MESSAGE, NotificationType.NEW_MESSAGE.getDefaultMessage(), "MESSAGE", "100L");

        assertThat(result.getRecipientId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(NotificationType.NEW_MESSAGE);
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.UNREAD);
        assertThat(result.getContent()).isEqualTo("새 메시지가 도착했습니다");
    }

    @Test
    @DisplayName("알림 읽음 처리")
    void markAsRead() {
        Notification unread = Notification.create(1L, NotificationType.SYSTEM_ANNOUNCEMENT, NotificationType.SYSTEM_ANNOUNCEMENT.getDefaultMessage(), "SYS", "10L");
        Notification read = unread.markAsRead();

        when(notificationRepository.findById("id1")).thenReturn(Optional.of(unread));
        when(notificationRepository.save(any())).thenReturn(read);

        Notification result = notificationService.markAsRead("id1");
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(result.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("알림 전체 삭제")
    void deleteAllByUser() {
        doNothing().when(notificationRepository).deleteAllByRecipientId(1L);
        notificationService.deleteAllNotifications(1L);
        verify(notificationRepository).deleteAllByRecipientId(1L);
    }
}
