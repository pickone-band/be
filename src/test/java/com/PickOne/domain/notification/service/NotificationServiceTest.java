package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.model.entity.NotificationDocument;
import com.PickOne.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        notificationService = new NotificationServiceImpl(notificationRepository, mock(SimpMessagingTemplate.class));
    }

    @Test
    @DisplayName("알림 전송")
    void sendNotification() {
        NotificationDocument dummy = NotificationDocument.builder()
                .id("123")
                .recipientId(1L)
                .type(NotificationType.MESSAGE_RECEIVED)
                .title("새 쪽지")
                .content("쪽지가 도착했습니다")
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any())).thenReturn(dummy);

        NotificationDocument result = notificationService.sendNotification(
                1L, NotificationType.MESSAGE_RECEIVED, "새 쪽지", "쪽지가 도착했습니다"
        );

        assertThat(result.getRecipientId()).isEqualTo(1L);
        assertThat(result.getType()).isEqualTo(NotificationType.MESSAGE_RECEIVED);
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.UNREAD);
        assertThat(result.getContent()).isEqualTo("쪽지가 도착했습니다");
    }

    @Test
    @DisplayName("알림 읽음 처리")
    void markAsRead() {
        NotificationDocument unread = NotificationDocument.builder()
                .id("id1")
                .recipientId(1L)
                .title("공지")
                .content("내용")
                .type(NotificationType.SYSTEM_ANNOUNCEMENT)
                .status(NotificationStatus.UNREAD)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findById("id1")).thenReturn(Optional.of(unread));
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        NotificationDocument result = notificationService.markAsRead("id1");

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(result.getReadAt()).isNotNull();
    }
}
