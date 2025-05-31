package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Message 도메인 객체를 위한 리포지토리 인터페이스
 */
public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(String id);
    List<Message> findByRecipientId(Long recipientId);
    List<Message> findBySenderId(Long senderId);
    List<Message> findRecentMessages(Long userId);
    void delete(String id);
}