package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.mapper.NotificationMapper;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.model.entity.NotificationDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB를 사용하는 NotificationRepository 구현
 */
@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationMongoRepository mongoRepository;

    @Override
    public Notification save(Notification notification) {
        NotificationDocument saved = mongoRepository.save(NotificationMapper.toDocument(notification));
        return NotificationMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(String id) {
        return mongoRepository.findById(id).map(NotificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByRecipientId(Long recipientId) {
        return mongoRepository.findByRecipientId(recipientId).stream()
                .map(NotificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByRecipientId(Long recipientId) {
        return mongoRepository.findByRecipientIdAndStatus(recipientId, "UNREAD").stream()
                .map(NotificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }

    @Override
    public void deleteAllByRecipientId(Long recipientId) {
        mongoRepository.deleteByRecipientId(recipientId);
    }
}
