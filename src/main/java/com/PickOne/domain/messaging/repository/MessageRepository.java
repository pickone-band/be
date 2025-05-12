package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Message domain object
 */
@Repository
public interface MessageRepository {

    /**
     * Save a message
     */
    Message save(Message message);

    /**
     * Find a message by its ID
     */
    Optional<Message> findById(String id);

    /**
     * Find all messages in a conversation between two users
     */
    Page<Message> findConversation(Long userId1, Long userId2, Pageable pageable);

    /**
     * Find all unread messages for a user
     */
    List<Message> findUnreadMessagesForUser(Long userId);

    /**
     * Find recent conversations for a user
     */
    List<Message> findRecentConversations(Long userId);

    /**
     * Count unread messages for a user
     */
    long countUnreadMessages(Long userId);
}