package com.PickOne.domain.messaging.controller;

import com.PickOne.domain.messaging.dto.SendMessageRequest;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.model.domain.MessageStatus;
import com.PickOne.domain.messaging.service.MessagingService;
import com.PickOne.global.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(
        controllers = MessageController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessagingService messagingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("메시지 전송 API")
    void sendMessage() throws Exception {
        Message message = new Message("1", 1L, 2L, "Hello", MessageStatus.SENT, LocalDateTime.now(), null, null);
        when(messagingService.sendMessage(1L, 2L, "Hello")).thenReturn(message);

        SendMessageRequest request = new SendMessageRequest(1L, 2L, "Hello");

        mockMvc.perform(post("/api/messages/send")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId").value(1L))
                .andExpect(jsonPath("$.recipientId").value(2L))
                .andExpect(jsonPath("$.content").value("Hello"));
    }

    @Test
    @DisplayName("읽지 않은 메시지 조회 API")
    void getUnreadMessages() throws Exception {
        List<Message> messages = List.of(new Message("1", 1L, 2L, "Hi", MessageStatus.SENT, LocalDateTime.now(), null, null));
        when(messagingService.getUnreadMessages(2L)).thenReturn(messages);

        mockMvc.perform(get("/api/messages/unread")
                        .param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("메시지 읽음 표시 API")
    void markAsRead() throws Exception {
        Message message = new Message("1", 1L, 2L, "Hi", MessageStatus.READ, LocalDateTime.now(), null, LocalDateTime.now());
        when(messagingService.markAsRead("1")).thenReturn(message);

        mockMvc.perform(post("/api/messages/1/read")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READ"));
    }

    @Test
    @DisplayName("메시지 삭제 API")
    void deleteMessage() throws Exception {
        doNothing().when(messagingService).deleteMessage("1");

        mockMvc.perform(delete("/api/messages/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
