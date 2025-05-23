package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.service.NotificationService;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 메시징 기능을 위한 서비스 인터페이스
 */
public interface MessagingService {

    /**
     * 한 사용자에서 다른 사용자로 메시지 전송
     */
    Message sendMessage(Long fromUserId, Long toUserId, String content);

    /**
     * 메시지를 전송됨으로 표시
     */
    Message markMessageDelivered(String messageId);

    /**
     * 메시지를 읽음으로 표시
     */
    Message markMessageRead(String messageId);

    /**
     * ID로 메시지 가져오기
     */
    Optional<Message> getMessage(String messageId);

    /**
     * 두 사용자 간의 대화 가져오기
     */
    Page<Message> getConversation(Long userId1, Long userId2, Pageable pageable);

    /**
     * 사용자의 읽지 않은 메시지 모두 가져오기
     */
    List<Message> getUnreadMessages(Long userId);

    /**
     * 사용자의 읽지 않은 메시지 수 세기
     */
    long countUnreadMessages(Long userId);

    /**
     * 사용자의 최근 대화 가져오기
     */
    List<Message> getRecentConversations(Long userId);
}