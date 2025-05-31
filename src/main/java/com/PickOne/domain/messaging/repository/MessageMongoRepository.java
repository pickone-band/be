package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.entity.MessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {

    List<MessageDocument> findBySenderId(Long senderId);
    List<MessageDocument> findByRecipientId(Long recipientId);

    // 최근 메시지 조회 (예: 상대방 기준으로 최신 하나씩만 뽑거나 전체 중 최신 정렬)
    @Query("{ '$or': [ { 'senderId': ?0 }, { 'recipientId': ?0 } ] }")
    List<MessageDocument> findRecentMessages(Long userId); // 임시 쿼리, 실제는 정렬 조건 필요
}