package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.document.MessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {

    List<MessageDocument> findByRoomIdOrderBySentAtAsc(Long roomId); // ✅ 채팅방 메시지

    List<MessageDocument> findBySenderId(Long senderId);

    MessageDocument findTopByRoomIdOrderBySentAtDesc(Long roomId); // ✅ 가장 최근 메시지
}