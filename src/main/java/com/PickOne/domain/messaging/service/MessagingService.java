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

    Message sendMessage(Long senderId, Long recipientId, String content);

    List<Message> getConversation(Long userId, Long otherUserId);

    List<Message> getUnreadMessages(Long userId);

    List<Message> getRecentMessages(Long userId);

    Message markAsRead(String messageId);

    void deleteMessage(String messageId);
}
