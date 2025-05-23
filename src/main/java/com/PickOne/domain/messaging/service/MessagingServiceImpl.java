package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.repository.MessageRepository;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.service.NotificationService;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * MessagingService의 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingServiceImpl implements MessagingService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic messageTopic;

    @Override
    public Message sendMessage(Long fromUserId, Long toUserId, String content) {
        // 두 사용자가 모두 존재하는지 확인
        userService.findById(fromUserId);
        userService.findById(toUserId);

        // 메시지 생성 및 저장
        Message message = Message.create(fromUserId, toUserId, content);
        Message savedMessage = messageRepository.save(message);

        // 수신자를 위한 알림 생성
        notificationService.createNotification(
                toUserId,
                NotificationType.NEW_MESSAGE,
                String.format("%s님이 새 메시지를 보냈습니다", userService.findById(fromUserId).getEmailValue()),
                "message",
                fromUserId
        );

        // 실시간 전달을 위해 Redis에 메시지 게시
        MessageDto messageDto = MessageDto.fromDomain(savedMessage);
        redisTemplate.convertAndSend(messageTopic.getTopic(), messageDto);

        log.info("사용자 {}가 사용자 {}에게 메시지를 전송했습니다", fromUserId, toUserId);
        return savedMessage;
    }

    @Override
    public Message markMessageDelivered(String messageId) {
        Message message = getMessage(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Message deliveredMessage = message.markDelivered();
        return messageRepository.save(deliveredMessage);
    }

    @Override
    public Message markMessageRead(String messageId) {
        Message message = getMessage(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        Message readMessage = message.markRead();
        return messageRepository.save(readMessage);
    }

    @Override
    public Optional<Message> getMessage(String messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public Page<Message> getConversation(Long userId1, Long userId2, Pageable pageable) {
        // 두 사용자가 모두 존재하는지 확인
        userService.findById(userId1);
        userService.findById(userId2);

        return messageRepository.findConversation(userId1, userId2, pageable);
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        return messageRepository.findUnreadMessagesForUser(userId);
    }

    @Override
    public long countUnreadMessages(Long userId) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        return messageRepository.countUnreadMessages(userId);
    }

    @Override
    public List<Message> getRecentConversations(Long userId) {
        // 사용자가 존재하는지 확인
        userService.findById(userId);

        return messageRepository.findRecentConversations(userId);
    }
}
