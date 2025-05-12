package com.PickOne.domain.messaging.repository;

import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.model.entity.MessageDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageRepositoryImplTest {

    @Mock
    private MessageMongoRepository messageMongoRepository;

    @InjectMocks
    private MessageRepositoryImpl messageRepository;

    @Test
    @DisplayName("메시지를 저장할 수 있다")
    void saveMessage() {
        // Given
        Message message = Message.create(1L, 2L, "테스트 메시지");
        MessageDocument document = MessageDocument.fromDomain(message);

        when(messageMongoRepository.save(any(MessageDocument.class))).thenReturn(document);

        // When
        Message savedMessage = messageRepository.save(message);

        // Then
        assertThat(savedMessage).isNotNull();
        assertThat(savedMessage.getId()).isEqualTo(message.getId());
        verify(messageMongoRepository).save(any(MessageDocument.class));
    }

    @Test
    @DisplayName("ID로 메시지를 조회할 수 있다")
    void findMessageById() {
        // Given
        String messageId = "test-id";
        MessageDocument document = new MessageDocument();
        document.setId(messageId);
        document.setSenderId(1L);
        document.setRecipientId(2L);
        document.setContent("테스트 메시지");
        document.setStatus(MessageStatus.SENT.name());
        document.setSentAt(LocalDateTime.now());

        when(messageMongoRepository.findById(messageId)).thenReturn(Optional.of(document));

        // When
        Optional<Message> foundMessage = messageRepository.findById(messageId);

        // Then
        assertThat(foundMessage).isPresent();
        assertThat(foundMessage.get().getId()).isEqualTo(messageId);
        verify(messageMongoRepository).findById(messageId);
    }

    @Test
    @DisplayName("두 사용자 간의 대화를 조회할 수 있다")
    void findConversation() {
        // Given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        MessageDocument document1 = new MessageDocument();
        document1.setId("msg1");
        document1.setSenderId(userId1);
        document1.setRecipientId(userId2);
        document1.setContent("안녕하세요");
        document1.setStatus(MessageStatus.READ.name());
        document1.setSentAt(LocalDateTime.now().minusMinutes(10));
        document1.setReadAt(LocalDateTime.now().minusMinutes(8));

        MessageDocument document2 = new MessageDocument();
        document2.setId("msg2");
        document2.setSenderId(userId2);
        document2.setRecipientId(userId1);
        document2.setContent("반갑습니다");
        document2.setStatus(MessageStatus.READ.name());
        document2.setSentAt(LocalDateTime.now().minusMinutes(5));
        document2.setReadAt(LocalDateTime.now().minusMinutes(3));

        Page<MessageDocument> documentPage = new PageImpl<>(Arrays.asList(document1, document2));
        when(messageMongoRepository.findConversation(userId1, userId2, pageable)).thenReturn(documentPage);

        // When
        Page<Message> conversation = messageRepository.findConversation(userId1, userId2, pageable);

        // Then
        assertThat(conversation).isNotNull();
        assertThat(conversation.getContent()).hasSize(2);
        assertThat(conversation.getContent().get(0).getSenderIdValue()).isEqualTo(userId1);
        assertThat(conversation.getContent().get(1).getSenderIdValue()).isEqualTo(userId2);
        verify(messageMongoRepository).findConversation(userId1, userId2, pageable);
    }

    @Test
    @DisplayName("사용자의 읽지 않은 메시지를 조회할 수 있다")
    void findUnreadMessagesForUser() {
        // Given
        Long userId = 1L;

        MessageDocument document1 = new MessageDocument();
        document1.setId("msg1");
        document1.setSenderId(2L);
        document1.setRecipientId(userId);
        document1.setContent("안녕하세요");
        document1.setStatus(MessageStatus.SENT.name());
        document1.setSentAt(LocalDateTime.now().minusMinutes(10));

        MessageDocument document2 = new MessageDocument();
        document2.setId("msg2");
        document2.setSenderId(3L);
        document2.setRecipientId(userId);
        document2.setContent("질문이 있습니다");
        document2.setStatus(MessageStatus.SENT.name());
        document2.setSentAt(LocalDateTime.now().minusMinutes(5));

        List<MessageDocument> documents = Arrays.asList(document1, document2);
        when(messageMongoRepository.findByRecipientIdAndStatus(userId, MessageStatus.SENT.name())).thenReturn(documents);

        // When
        List<Message> unreadMessages = messageRepository.findUnreadMessagesForUser(userId);

        // Then
        assertThat(unreadMessages).hasSize(2);
        assertThat(unreadMessages.get(0).getStatus()).isEqualTo(MessageStatus.SENT);
        assertThat(unreadMessages.get(1).getStatus()).isEqualTo(MessageStatus.SENT);
        verify(messageMongoRepository).findByRecipientIdAndStatus(userId, MessageStatus.SENT.name());
    }

    @Test
    @DisplayName("사용자의 최근 대화 목록을 조회할 수 있다")
    void findRecentConversations() {
        // Given
        Long userId = 1L;

        MessageDocument document1 = new MessageDocument();
        document1.setId("msg1");
        document1.setSenderId(userId);
        document1.setRecipientId(2L);
        document1.setContent("안녕하세요");
        document1.setStatus(MessageStatus.READ.name());
        document1.setSentAt(LocalDateTime.now().minusMinutes(30));

        MessageDocument document2 = new MessageDocument();
        document2.setId("msg2");
        document2.setSenderId(3L);
        document2.setRecipientId(userId);
        document2.setContent("질문이 있습니다");
        document2.setStatus(MessageStatus.SENT.name());
        document2.setSentAt(LocalDateTime.now().minusMinutes(5));

        List<MessageDocument> documents = Arrays.asList(document1, document2);
        when(messageMongoRepository.findRecentConversations(userId)).thenReturn(documents);

        // When
        List<Message> recentConversations = messageRepository.findRecentConversations(userId);

        // Then
        assertThat(recentConversations).hasSize(2);
        verify(messageMongoRepository).findRecentConversations(userId);
    }

    @Test
    @DisplayName("사용자의 읽지 않은 메시지 수를 조회할 수 있다")
    void countUnreadMessages() {
        // Given
        Long userId = 1L;
        long expectedCount = 5L;

        when(messageMongoRepository.countByRecipientIdAndStatus(userId, MessageStatus.SENT.name())).thenReturn(expectedCount);

        // When
        long count = messageRepository.countUnreadMessages(userId);

        // Then
        assertThat(count).isEqualTo(expectedCount);
        verify(messageMongoRepository).countByRecipientIdAndStatus(userId, MessageStatus.SENT.name());
    }
}
