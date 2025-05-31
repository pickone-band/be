package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.UserUpdateRequest;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 정보 조회 API - 성공")
    void getUserById_success() throws Exception {
        Long userId = 1L;
        Email email = Email.of("test@example.com");
        Password password = Password.ofEncoded("hashedPass");
        User user = User.of(userId, email, password, "nickname", true);
        when(userService.findById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    @DisplayName("회원 정보 수정 API - 성공")
    void updateUser_success() throws Exception {
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest("new@example.com", "newnick", true);
        Email newEmail = Email.of(request.email());
        Password password = Password.ofEncoded("existingPass");
        User updated = User.of(userId, newEmail, password, request.nickname(), request.isPublic());

        when(userService.updateUser(eq(userId), any())).thenReturn(updated);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.nickname").value("newnick"))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    @DisplayName("회원 탈퇴 API - 성공")
    void deleteUser_success() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
