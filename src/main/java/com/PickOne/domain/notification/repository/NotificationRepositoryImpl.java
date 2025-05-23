package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.model.entity.NotificationDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB를 사용하는 NotificationRepository 구현
 */
@Component
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationMongoRepository notificationMongoRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationDocument document = NotificationDocument.fromDomain(notification);
        NotificationDocument savedDocument = notificationMongoRepository.save(document);
        return savedDocument.toDomain();
    }

    @Override
    public Optional<Notification> findById(String id) {
        return notificationMongoRepository.findById(id)
                .map(NotificationDocument::toDomain);
    }

    @Override
    public Page<Notification> findAllForUser(Long userId, Pageable pageable) {
        return notificationMongoRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
                .map(NotificationDocument::toDomain);
    }

    @Override
    public List<Notification> findUnreadForUser(Long userId) {
        return notificationMongoRepository.findByRecipientIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        NotificationStatus.UNREAD.name()
                )
                .stream()
                .map(NotificationDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadForUser(Long userId) {
        return notificationMongoRepository.countByRecipientIdAndStatus(
                userId,
                NotificationStatus.UNREAD.name()
        );
    }

    @Override
    public Page<Notification> findByTypeForUser(Long userId, NotificationType type, Pageable pageable) {
        return notificationMongoRepository.findByRecipientIdAndTypeOrderByCreatedAtDesc(
                        userId,
                        type.name(),
                        pageable
                )
                .map(NotificationDocument::toDomain);
    }

    @Override
    public void deleteAll(){
        notificationMongoRepository.deleteAll();
    }
}