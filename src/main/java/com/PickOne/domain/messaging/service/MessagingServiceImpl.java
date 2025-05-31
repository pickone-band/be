package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MessagingService의 구현
 */
@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

    private final MessageRepository messageRepository;

    @Override
    public Message sendMessage(Long senderId, Long recipientId, String content) {
        Message message = Message.create(senderId, recipientId, content);
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getConversation(Long userId, Long otherUserId) {
        List<Message> received = messageRepository.findBySenderId(otherUserId).stream()
                .filter(msg -> msg.getRecipientId().equals(userId))
                .collect(Collectors.toList());

        List<Message> sent = messageRepository.findBySenderId(userId).stream()
                .filter(msg -> msg.getRecipientId().equals(otherUserId))
                .collect(Collectors.toList());

        received.addAll(sent);
        return received.stream()
                .sorted((a, b) -> a.getSentAt().compareTo(b.getSentAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByRecipientId(userId).stream()
                .filter(msg -> msg.getStatus() != MessageStatus.READ)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getRecentMessages(Long userId) {
        return messageRepository.findRecentMessages(userId);
    }

    @Override
    public Message markAsRead(String messageId) {
        return messageRepository.findById(messageId)
                .map(Message::markAsRead)
                .map(messageRepository::save)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다: " + messageId));
    }

    @Override
    public void deleteMessage(String messageId) {
        messageRepository.delete(messageId);
    }
}