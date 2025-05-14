package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.repository.NotificationRepository;
import com.PickOne.domain.notification.service.NotificationService;
import com.PickOne.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Transactional
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    // UserService 모킹 - 대상 사용자가 존재한다고 가정
    @MockBean
    private UserService userService;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void clean() {
        notificationRepository.deleteAll();
    }

    @Test
    public void testCreateAndReadNotification() {
        // UserService 모킹
        Long recipientId = 3L;
        when(userService.findById(recipientId)).thenReturn(null); // 반환값은 중요하지 않음

        // 알림 생성
        Notification notification = notificationService.createNotification(
                recipientId,
                NotificationType.SYSTEM_ANNOUNCEMENT,
                "Service Test Notification",
                "TEST",
                1L
        );

        assertNotNull(notification);
        assertNotNull(notification.getId());
        assertEquals(NotificationStatus.UNREAD, notification.getStatus());

        // 알림 조회
        Optional<Notification> found = notificationService.getNotification(notification.getId());
        assertTrue(found.isPresent());

        // 알림 읽음 표시
        Notification readNotification = notificationService.markNotificationRead(notification.getId());
        assertEquals(NotificationStatus.READ, readNotification.getStatus());

        // 알림 조회
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(recipientId);
        assertFalse(unreadNotifications.stream().anyMatch(n -> n.getId().equals(notification.getId())));

        // 검증
        verify(userService, times(2)).findById(recipientId);
    }
    @Test
    public void testMarkAllNotificationsRead() {
        // UserService 모킹
        Long recipientId = 4L;
        when(userService.findById(recipientId)).thenReturn(null);

        // 알림 생성 (2개)
        notificationService.createNotification(
                recipientId, NotificationType.SYSTEM_ANNOUNCEMENT, "Test 1", "TEST", 1L);
        notificationService.createNotification(
                recipientId, NotificationType.SYSTEM_ANNOUNCEMENT, "Test 2", "TEST", 2L);

        // 모든 알림 읽음으로 표시
        notificationService.markAllNotificationsReadForUser(recipientId);

        // 읽지 않은 알림 조회
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(recipientId);
        assertEquals(0, unreadNotifications.size());

        // 유형별 알림 조회
        long count = notificationService.getNotificationsByTypeForUser(
                recipientId, NotificationType.SYSTEM_ANNOUNCEMENT, PageRequest.of(0, 10)).getTotalElements();
        assertEquals(2, count);
    }
}