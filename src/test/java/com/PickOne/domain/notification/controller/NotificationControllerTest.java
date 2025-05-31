package com.PickOne.domain.notification.controller;

import com.PickOne.domain.consent.controller.ConsentController;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.service.NotificationService;
import com.PickOne.global.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(
        controllers = NotificationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("알림 생성 API")
    void sendNotification() throws Exception {
        Notification notification = Notification.create(1L, NotificationType.NEW_MESSAGE, NotificationType.NEW_MESSAGE.getDefaultMessage(), "MESSAGE", "100");
        when(notificationService.createNotification(any(), any(), any(), any(), any())).thenReturn(notification);

        mockMvc.perform(post("/api/notifications/send")
                        .param("recipientId", "1")
                        .param("type", "NEW_MESSAGE")
                        .param("content", "새 메시지가 도착했습니다")
                        .param("refEntityType", "MESSAGE")
                        .param("refEntityId", "100")
                        .with(csrf())) // ✅ CSRF 토큰 포함
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("읽지 않은 알림 조회 API")
    void getUnreadNotifications() throws Exception {
        Notification notification = Notification.create(1L, NotificationType.SYSTEM_ANNOUNCEMENT, NotificationType.SYSTEM_ANNOUNCEMENT.getDefaultMessage(), "SYSTEM", "200L");
        when(notificationService.getUnreadNotifications(1L)).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/notifications/unread")
                        .param("recipientId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("알림 읽음 처리 API")
    void markAsRead() throws Exception {
        Notification read = Notification.create(1L, NotificationType.SYSTEM_ANNOUNCEMENT, "공지", "SYS", "300L").markAsRead();
        when(notificationService.markAsRead("id1")).thenReturn(read);

        mockMvc.perform(post("/api/notifications/id1/read")
                        .with(csrf())) // ✅ POST 요청에 반드시 필요
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알림 삭제 API")
    void deleteNotification() throws Exception {
        mockMvc.perform(delete("/api/notifications/id1")
                        .with(csrf())) // ✅ DELETE 요청에도 필요
                .andExpect(status().isNoContent());

    }
}
