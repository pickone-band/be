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
@Repository
public interface MessageRepository {

    /**
     * 메시지 저장
     */
    Message save(Message message);

    /**
     * ID로 메시지 찾기
     */
    Optional<Message> findById(String id);

    /**
     * 두 사용자 간의 대화에서 모든 메시지 찾기
     */
    Page<Message> findConversation(Long userId1, Long userId2, Pageable pageable);

    /**
     * 사용자의 읽지 않은 메시지 모두 찾기
     */
    List<Message> findUnreadMessagesForUser(Long userId);

    /**
     * 사용자의 최근 대화 찾기
     */
    List<Message> findRecentConversations(Long userId);

    /**
     * 사용자의 읽지 않은 메시지 수 세기
     */
    long countUnreadMessages(Long userId);
}