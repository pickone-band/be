package com.PickOne.domain.notification.service;

import com.PickOne.domain.notification.dto.NotificationDto;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.repository.NotificationRepository;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * NotificationService의 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic notificationTopic;

    @Override
    public Notification createNotification(Long recipientId, NotificationType type, String content,
                                           String refEntityType, Long refEntityId) {
        // 수신자가 존재하는지 확인
        userService.findById(recipientId);

        // 알림 생성 및 저장
        Notification notification = Notification.create(recipientId, type, content, refEntityType, refEntityId);
        Notification savedNotification = notificationRepository.save(notification);

        // 실시간 전달을 위해 Redis에 알림 게시
        NotificationDto notificationDto = NotificationDto.fromDomain(savedNotification);
        redisTemplate.convertAndSend(notificationTopic.getTopic(), notificationDto);

        log.info("사용자 {}를 위한 알림이 생성되었습니다: {}", recipientId, content);
        return savedNotification;
    }

    @Override
    public Notification markNotificationRead(String notificationId) {
        Notification notification = getNotification(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        if (notification.getStatus() == NotificationStatus.READ) {
            return notification;
        }

        Notification readNotification = notification.markRead();
        return notificationRepository.save(readNotification);
    }

    @Override
    public void markAllNotificationsReadForUser(Long userId) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        // 모든 읽지 않은 알림 가져오기
        List<Notification> unreadNotifications = notificationRepository.findUnreadForUser(userId);

        // 각각을 읽음으로 표시하고 저장
        unreadNotifications.forEach(notification -> {
            Notification readNotification = notification.markRead();
            notificationRepository.save(readNotification);
        });

        log.info("사용자 {}의 {}개 알림이 읽음으로 표시되었습니다", userId, unreadNotifications.size());
    }

    @Override
    public Optional<Notification> getNotification(String notificationId) {
        return notificationRepository.findById(notificationId);
    }

    @Override
    public Page<Notification> getAllNotificationsForUser(Long userId, Pageable pageable) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        return notificationRepository.findAllForUser(userId, pageable);
    }

    @Override
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        return notificationRepository.findUnreadForUser(userId);
    }

    @Override
    public long countUnreadNotificationsForUser(Long userId) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        return notificationRepository.countUnreadForUser(userId);
    }

    @Override
    public Page<Notification> getNotificationsByTypeForUser(Long userId, NotificationType type, Pageable pageable) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        return notificationRepository.findByTypeForUser(userId, type, pageable);
    }
}