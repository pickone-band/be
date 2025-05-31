package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.mapper.MessageMapper;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.model.entity.MessageDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB를 사용하는 MessageRepository 구현
 */
@Repository
@RequiredArgsConstructor
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageMongoRepository mongoRepository;

    @Override
    public Message save(Message message) {
        MessageDocument saved = mongoRepository.save(MessageMapper.toDocument(message));
        return MessageMapper.toDomain(saved);
    }

    @Override
    public Optional<Message> findById(String id) {
        return mongoRepository.findById(id).map(MessageMapper::toDomain);
    }

    @Override
    public List<Message> findByRecipientId(Long recipientId) {
        return mongoRepository.findByRecipientId(recipientId).stream()
                .map(MessageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findBySenderId(Long senderId) {
        return mongoRepository.findBySenderId(senderId).stream()
                .map(MessageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findRecentMessages(Long userId) {
        return mongoRepository.findRecentMessages(userId).stream()
                .map(MessageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        mongoRepository.deleteById(id);
    }
}
