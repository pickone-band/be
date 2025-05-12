package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.repository.MessageRepository;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.service.NotificationService;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingServiceImplTest {

  @Mock private MessageRepository messageRepository;

  @Mock private UserService userService;

  @Mock private NotificationService notificationService;

  @Mock private RedisTemplate<String, Object> redisTemplate;

  @Mock private ChannelTopic messageTopic;

  @InjectMocks private MessagingServiceImpl messagingService;

    @Test
    @DisplayName("메시지를 전송할 수 있다")
    void sendMessage() {
        // Given
        Long senderId = 1L;
        Long recipientId = 2L;
        String content = "테스트 메시지입니다";
        String channelTopic = "message.topic";

        User sender = mock(User.class);
        when(sender.getEmailValue()).thenReturn("sender@example.com");

        when(userService.findById(senderId)).thenReturn(sender);
        when(userService.findById(recipientId)).thenReturn(mock(User.class));
        when(messageTopic.getTopic()).thenReturn(channelTopic);

        // MessageRepository.save를 통해 저장된 메시지 반환
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        when(messageRepository.save(messageCaptor.capture())).thenAnswer(invocation -> {
            Message message = messageCaptor.getValue();
            return message;
        });

        // When
        Message result = messagingService.sendMessage(senderId, recipientId, content);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSenderIdValue()).isEqualTo(senderId);
        assertThat(result.getRecipientIdValue()).isEqualTo(recipientId);
        assertThat(result.getContentValue()).isEqualTo(content);

        // senderId가 두 번 호출되는 것을 검증
        verify(userService, times(2)).findById(senderId);
        verify(userService).findById(recipientId);
        verify(messageRepository).save(any(Message.class));
        verify(notificationService).createNotification(
                eq(recipientId),
                eq(NotificationType.NEW_MESSAGE),
                contains("새 메시지를 보냈습니다"),
                eq("message"),
                eq(senderId)
        );
        verify(redisTemplate).convertAndSend(eq(channelTopic), any(MessageDto.class));
    }

  @Test
  @DisplayName("메시지를 배달됨으로 표시할 수 있다")
  void markMessageDelivered() {
    // Given
    String messageId = "test-id";
    Message message = Message.create(1L, 2L, "테스트 메시지");

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageRepository.save(any(Message.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Message result = messagingService.markMessageDelivered(messageId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(MessageStatus.DELIVERED);
    assertThat(result.getDeliveredAt()).isNotNull();

    verify(messageRepository).findById(messageId);
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("존재하지 않는 메시지 ID로 배달됨 표시를 시도하면 예외가 발생한다")
  void markNonExistentMessageDelivered() {
    // Given
    String messageId = "non-existent-id";
    when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

    // When, Then
    assertThatThrownBy(() -> messagingService.markMessageDelivered(messageId))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ENTITY_NOT_FOUND);

    verify(messageRepository).findById(messageId);
    verify(messageRepository, never()).save(any(Message.class));
  }

  @Test
  @DisplayName("메시지를 읽음으로 표시할 수 있다")
  void markMessageRead() {
    // Given
    String messageId = "test-id";
    Message message = Message.create(1L, 2L, "테스트 메시지");

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
    when(messageRepository.save(any(Message.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    Message result = messagingService.markMessageRead(messageId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(MessageStatus.READ);
    assertThat(result.getDeliveredAt()).isNotNull();
    assertThat(result.getReadAt()).isNotNull();

    verify(messageRepository).findById(messageId);
    verify(messageRepository).save(any(Message.class));
  }

  @Test
  @DisplayName("두 사용자 간의 대화를 조회할 수 있다")
  void getConversation() {
    // Given
    Long userId1 = 1L;
    Long userId2 = 2L;
    Pageable pageable = PageRequest.of(0, 20);

    Message message1 = Message.create(userId1, userId2, "안녕하세요");
    Message message2 = Message.create(userId2, userId1, "반갑습니다");
    Page<Message> messagePage = new PageImpl<>(Arrays.asList(message1, message2));

    when(userService.findById(userId1)).thenReturn(mock(User.class));
    when(userService.findById(userId2)).thenReturn(mock(User.class));
    when(messageRepository.findConversation(userId1, userId2, pageable)).thenReturn(messagePage);

    // When
    Page<Message> result = messagingService.getConversation(userId1, userId2, pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getSenderIdValue()).isEqualTo(userId1);
    assertThat(result.getContent().get(1).getSenderIdValue()).isEqualTo(userId2);

    verify(userService).findById(userId1);
    verify(userService).findById(userId2);
    verify(messageRepository).findConversation(userId1, userId2, pageable);
  }

  @Test
  @DisplayName("사용자의 읽지 않은 메시지를 조회할 수 있다")
  void getUnreadMessages() {
    // Given
    Long userId = 1L;
    Message message1 = Message.create(2L, userId, "안녕하세요");
    Message message2 = Message.create(3L, userId, "질문이 있습니다");
    List<Message> unreadMessages = Arrays.asList(message1, message2);

    when(userService.findById(userId)).thenReturn(mock(User.class));
    when(messageRepository.findUnreadMessagesForUser(userId)).thenReturn(unreadMessages);

    // When
    List<Message> result = messagingService.getUnreadMessages(userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(unreadMessages);

    verify(userService).findById(userId);
    verify(messageRepository).findUnreadMessagesForUser(userId);
  }

  @Test
  @DisplayName("사용자의 읽지 않은 메시지 수를 조회할 수 있다")
  void countUnreadMessages() {
    // Given
    Long userId = 1L;
    long expectedCount = 5L;

    when(userService.findById(userId)).thenReturn(mock(User.class));
    when(messageRepository.countUnreadMessages(userId)).thenReturn(expectedCount);

    // When
    long result = messagingService.countUnreadMessages(userId);

    // Then
    assertThat(result).isEqualTo(expectedCount);

    verify(userService).findById(userId);
    verify(messageRepository).countUnreadMessages(userId);
  }

  @Test
  @DisplayName("사용자의 최근 대화 목록을 조회할 수 있다")
  void getRecentConversations() {
    // Given
    Long userId = 1L;
    Message message1 = Message.create(userId, 2L, "첫 번째 대화");
    Message message2 = Message.create(3L, userId, "두 번째 대화");
    List<Message> recentConversations = Arrays.asList(message1, message2);

    when(userService.findById(userId)).thenReturn(mock(User.class));
    when(messageRepository.findRecentConversations(userId)).thenReturn(recentConversations);

    // When
    List<Message> result = messagingService.getRecentConversations(userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyElementsOf(recentConversations);

    verify(userService).findById(userId);
    verify(messageRepository).findRecentConversations(userId);
  }

  @Test
  @DisplayName("메시지를 ID로 조회할 수 있다")
  void getMessage() {
    // Given
    String messageId = "test-id";
    Message message = Message.create(1L, 2L, "테스트 메시지");

    when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

    // When
    Optional<Message> result = messagingService.getMessage(messageId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(message);

    verify(messageRepository).findById(messageId);
  }

  @Test
  @DisplayName("존재하지 않는 메시지 ID로 조회하면 빈 Optional을 반환한다")
  void getNonExistentMessage() {
    // Given
    String messageId = "non-existent-id";
    when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

    // When
    Optional<Message> result = messagingService.getMessage(messageId);

    // Then
    assertThat(result).isEmpty();
    verify(messageRepository).findById(messageId);
  }
}
