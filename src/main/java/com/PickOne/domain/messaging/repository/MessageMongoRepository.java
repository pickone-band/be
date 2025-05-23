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
     * 두 사용자 간의 모든 메시지 찾기
     */
    @Query("{ $or: [ { 'senderId': ?0, 'recipientId': ?1 }, { 'senderId': ?1, 'recipientId': ?0 } ] }")
    Page<MessageDocument> findConversation(Long userId1, Long userId2, Pageable pageable);

    /**
     * 상태가 SENT(읽지 않음)인 사용자에게 전송된 메시지 찾기
     */
    List<MessageDocument> findByRecipientIdAndStatus(Long recipientId, String status);

    /**
     * 사용자의 최근 대화 찾기
     */
    @Query(value = "{ $or: [ { 'senderId': ?0 }, { 'recipientId': ?0 } ] }",
            sort = "{ 'sentAt': -1 }")
    List<MessageDocument> findRecentConversations(Long userId);

    /**
     * 사용자의 읽지 않은 메시지 수 세기
     */
    long countByRecipientIdAndStatus(Long recipientId, String status);
}