package com.PickOne.domain.messaging.controller;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.service.MessagingService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.security.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessagingService messagingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "1")
    @DisplayName("메시지를 전송할 수 있다")
    void sendMessage() throws Exception {
        // Given
        Long recipientId = 2L;
        String content = "테스트 메시지입니다";

        Message message = Message.create(1L, recipientId, content);

        when(messagingService.sendMessage(eq(1L), eq(recipientId), eq(content)))
                .thenReturn(message);

        // When
        ResultActions result = mockMvc.perform(post("/api/messages/send/{recipientId}", recipientId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.senderId").value(1))
                .andExpect(jsonPath("$.result.recipientId").value(recipientId))
                .andExpect(jsonPath("$.result.content").value(content));

        verify(messagingService).sendMessage(eq(1L), eq(recipientId), eq(content));
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("다른 사용자와의 대화를 조회할 수 있다")
    void getConversation() throws Exception {
        // Given
        Long userId = 2L;
        Long currentUserId = 1L;

        Message message1 = mockMessage("1", currentUserId, userId, "안녕하세요", MessageStatus.READ);
        Message message2 = mockMessage("2", userId, currentUserId, "반갑습니다", MessageStatus.READ);

        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message1, message2),
                PageRequest.of(0, 20), 2);

        when(messagingService.getConversation(eq(currentUserId), eq(userId), any(Pageable.class)))
                .thenReturn(messagePage);

        // When
        ResultActions result = mockMvc.perform(get("/api/messages/conversation/{userId}", userId)
                .param("page", "0")
                .param("size", "20")
                .param("sort", "sentAt,desc"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.content", hasSize(2)))
                .andExpect(jsonPath("$.result.content[0].senderId").value(currentUserId))
                .andExpect(jsonPath("$.result.content[1].senderId").value(userId));

        verify(messagingService).getConversation(eq(currentUserId), eq(userId), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("메시지를 읽음 상태로 표시할 수 있다")
    void markMessageRead() throws Exception {
        // Given
        String messageId = "test-message-id";
        Message message = mockMessage(messageId, 2L, 1L, "테스트 메시지", MessageStatus.SENT);
        Message readMessage = mockMessage(messageId, 2L, 1L, "테스트 메시지", MessageStatus.READ);

        when(messagingService.markMessageRead(messageId)).thenReturn(readMessage);

        // When
        ResultActions result = mockMvc.perform(post("/api/messages/{messageId}/read", messageId)
                .with(csrf()));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.id").value(messageId))
                .andExpect(jsonPath("$.result.status").value("READ"));

        verify(messagingService).markMessageRead(messageId);
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("읽지 않은 메시지를 조회할 수 있다")
    void getUnreadMessages() throws Exception {
        // Given
        Long currentUserId = 1L;

        Message message1 = mockMessage("1", 2L, currentUserId, "안녕하세요", MessageStatus.SENT);
        Message message2 = mockMessage("2", 3L, currentUserId, "질문이 있습니다", MessageStatus.SENT);

        List<Message> unreadMessages = Arrays.asList(message1, message2);
        when(messagingService.getUnreadMessages(currentUserId)).thenReturn(unreadMessages);

        // When
        ResultActions result = mockMvc.perform(get("/api/messages/unread"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[0].senderId").value(2))
                .andExpect(jsonPath("$.result[1].senderId").value(3));

        verify(messagingService).getUnreadMessages(currentUserId);
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("읽지 않은 메시지 수를 조회할 수 있다")
    void getUnreadMessageCount() throws Exception {
        // Given
        Long currentUserId = 1L;
        long unreadCount = 5L;

        when(messagingService.countUnreadMessages(currentUserId)).thenReturn(unreadCount);

        // When
        ResultActions result = mockMvc.perform(get("/api/messages/unread/count"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(unreadCount));

        verify(messagingService).countUnreadMessages(currentUserId);
    }

    @Test
    @WithMockUser(username = "1")
    @DisplayName("최근 대화 목록을 조회할 수 있다")
    void getRecentConversations() throws Exception {
        // Given
        Long currentUserId = 1L;

        Message message1 = mockMessage("1", currentUserId, 2L, "안녕하세요", MessageStatus.READ);
        Message message2 = mockMessage("2", 3L, currentUserId, "질문이 있습니다", MessageStatus.SENT);

        List<Message> recentConversations = Arrays.asList(message1, message2);
        when(messagingService.getRecentConversations(currentUserId)).thenReturn(recentConversations);

        // When
        ResultActions result = mockMvc.perform(get("/api/messages/recent"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result", hasSize(2)))
                .andExpect(jsonPath("$.result[0].senderId").value(currentUserId))
                .andExpect(jsonPath("$.result[1].senderId").value(3));

        verify(messagingService).getRecentConversations(currentUserId);
    }

    // WebSocket 테스트는 별도의 통합 테스트로 작성하는 것이 좋습니다.
    // 여기서는 컨트롤러의 기본 REST API만 테스트합니다.

    // Helper method to mock a Message instance
    private Message mockMessage(String id, Long senderId, Long recipientId, String content, MessageStatus status) {
        LocalDateTime now = LocalDateTime.now();
        Message message = mock(Message.class);

        when(message.getId()).thenReturn(id);
        when(message.getSenderIdValue()).thenReturn(senderId);
        when(message.getRecipientIdValue()).thenReturn(recipientId);
        when(message.getContentValue()).thenReturn(content);
        when(message.getStatus()).thenReturn(status);
        when(message.getSentAt()).thenReturn(now.minusMinutes(5));

        if (status == MessageStatus.DELIVERED || status == MessageStatus.READ) {
            when(message.getDeliveredAt()).thenReturn(now.minusMinutes(3));
        }

        if (status == MessageStatus.READ) {
            when(message.getReadAt()).thenReturn(now.minusMinutes(1));
        }

        return message;
    }
}