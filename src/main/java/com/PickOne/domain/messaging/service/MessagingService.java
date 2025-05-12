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
 * Service interface for messaging functionality
 */
public interface MessagingService {

    /**
     * Send a message from one user to another
     */
    Message sendMessage(Long fromUserId, Long toUserId, String content);

    /**
     * Mark a message as delivered
     */
    Message markMessageDelivered(String messageId);

    /**
     * Mark a message as read
     */
    Message markMessageRead(String messageId);

    /**
     * Get a message by its ID
     */
    Optional<Message> getMessage(String messageId);

    /**
     * Get a conversation between two users
     */
    Page<Message> getConversation(Long userId1, Long userId2, Pageable pageable);

    /**
     * Get all unread messages for a user
     */
    List<Message> getUnreadMessages(Long userId);

    /**
     * Count unread messages for a user
     */
    long countUnreadMessages(Long userId);

    /**
     * Get recent conversations for a user
     */
    List<Message> getRecentConversations(Long userId);
}