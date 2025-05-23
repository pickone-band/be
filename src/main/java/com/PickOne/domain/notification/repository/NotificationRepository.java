package com.PickOne.domain.notification.repository;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Notification 도메인 객체를 위한 리포지토리 인터페이스
 */
@Repository
public interface NotificationRepository {

    /**
     * 알림 저장
     */
    Notification save(Notification notification);

    /**
     * ID로 알림 찾기
     */
    Optional<Notification> findById(String id);

    /**
     * 사용자의 모든 알림 찾기
     */
    Page<Notification> findAllForUser(Long userId, Pageable pageable);

    /**
     * 사용자의 읽지 않은 알림 찾기
     */
    List<Notification> findUnreadForUser(Long userId);

    /**
     * 사용자의 읽지 않은 알림 수 세기
     */
    long countUnreadForUser(Long userId);

    /**
     * 사용자의 유형별 알림 찾기
     */
    Page<Notification> findByTypeForUser(Long userId, NotificationType type, Pageable pageable);

    void deleteAll();
}