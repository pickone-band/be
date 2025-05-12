package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMongoRepository notificationMongoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testMongoConnection() {
        assertNotNull(mongoTemplate);
        System.out.println("MongoDB connection successful");
    }

    @Test
    public void testNotificationRepositories() {
        assertNotNull(notificationRepository);
        assertNotNull(notificationMongoRepository);
        System.out.println("Notification repositories successfully autowired");
    }

    @Test
    public void testSaveAndFindNotification() {
        // 테스트 데이터 생성
        Long recipientId = 1L;
        NotificationType type = NotificationType.SYSTEM_ANNOUNCEMENT;
        String content = "Test Notification";
        String refEntityType = "TEST";
        Long refEntityId = 1L;

        // Notification 생성
        Notification notification = Notification.create(recipientId, type, content, refEntityType, refEntityId);

        // 저장
        Notification savedNotification = notificationRepository.save(notification);
        assertNotNull(savedNotification.getId());

        // ID로 조회
        Optional<Notification> foundOptional = notificationRepository.findById(savedNotification.getId());
        assertTrue(foundOptional.isPresent());

        Notification found = foundOptional.get();
        assertEquals(recipientId, found.getRecipientIdValue());
        assertEquals(type, found.getType());
        assertEquals(content, found.getContentValue());
        assertEquals(NotificationStatus.UNREAD, found.getStatus());

        // 삭제
        notificationMongoRepository.deleteById(savedNotification.getId());
    }

    @Test
    public void testFindUnreadNotifications() {
        // 테스트 데이터 추가 (3개의 알림, 2개는 읽지 않음)
        Long recipientId = 2L;

        // 읽지 않은 알림 1
        Notification notification1 = Notification.create(
                recipientId, NotificationType.SYSTEM_ANNOUNCEMENT, "Unread 1", "TEST", 1L);
        notificationRepository.save(notification1);

        // 읽지 않은 알림 2
        Notification notification2 = Notification.create(
                recipientId, NotificationType.SYSTEM_ANNOUNCEMENT, "Unread 2", "TEST", 2L);
        notificationRepository.save(notification2);

        // 읽은 알림
        Notification notification3 = Notification.create(
                recipientId, NotificationType.SYSTEM_ANNOUNCEMENT, "Read 1", "TEST", 3L);
        notification3 = notification3.markRead();
        notificationRepository.save(notification3);

        // 읽지 않은 알림 조회
        List<Notification> unreadNotifications = notificationRepository.findUnreadForUser(recipientId);
        assertEquals(2, unreadNotifications.size());

        // 읽지 않은 알림 수 조회
        long unreadCount = notificationRepository.countUnreadForUser(recipientId);
        assertEquals(2, unreadCount);

        // 유형별 알림 조회
        assertEquals(3, notificationRepository.findByTypeForUser(
                recipientId, NotificationType.SYSTEM_ANNOUNCEMENT, PageRequest.of(0, 10)).getTotalElements());

        // 테스트 데이터 정리
        notificationMongoRepository.deleteById(notification1.getId());
        notificationMongoRepository.deleteById(notification2.getId());
        notificationMongoRepository.deleteById(notification3.getId());
    }
}