package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.entity.MessageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {

    /**
     * Find all messages between two users
     */
    @Query("{ $or: [ { 'senderId': ?0, 'recipientId': ?1 }, { 'senderId': ?1, 'recipientId': ?0 } ] }")
    Page<MessageDocument> findConversation(Long userId1, Long userId2, Pageable pageable);

    /**
     * Find messages sent to a user with status = SENT (unread)
     */
    List<MessageDocument> findByRecipientIdAndStatus(Long recipientId, String status);

    /**
     * Find recent conversations for a user
     */
    @Query(value = "{ $or: [ { 'senderId': ?0 }, { 'recipientId': ?0 } ] }",
            sort = "{ 'sentAt': -1 }")
    List<MessageDocument> findRecentConversations(Long userId);

    /**
     * Count unread messages for a user
     */
    long countByRecipientIdAndStatus(Long recipientId, String status);
}