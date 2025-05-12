package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.model.entity.MessageDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of MessageRepository using MongoDB
 */
@Component
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageMongoRepository messageMongoRepository;

    @Override
    public Message save(Message message) {
        MessageDocument document = MessageDocument.fromDomain(message);
        MessageDocument savedDocument = messageMongoRepository.save(document);
        return savedDocument.toDomain();
    }

    @Override
    public Optional<Message> findById(String id) {
        return messageMongoRepository.findById(id)
                .map(MessageDocument::toDomain);
    }

    @Override
    public Page<Message> findConversation(Long userId1, Long userId2, Pageable pageable) {
        return messageMongoRepository.findConversation(userId1, userId2, pageable)
                .map(MessageDocument::toDomain);
    }

    @Override
    public List<Message> findUnreadMessagesForUser(Long userId) {
        return messageMongoRepository.findByRecipientIdAndStatus(userId, MessageStatus.SENT.name())
                .stream()
                .map(MessageDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findRecentConversations(Long userId) {
        return messageMongoRepository.findRecentConversations(userId)
                .stream()
                .map(MessageDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countUnreadMessages(Long userId) {
        return messageMongoRepository.countByRecipientIdAndStatus(userId, MessageStatus.SENT.name());
    }
}