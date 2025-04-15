package com.PickOne.user.controller;

import com.PickOne.common.config.SecurityConfig;
import com.PickOne.user.controller.dto.PasswordChangeRequest;
import com.PickOne.user.controller.dto.UserRegistrationRequest;
import com.PickOne.user.exception.EmailAlreadyExistsException;
import com.PickOne.user.exception.PasswordMismatchException;
import com.PickOne.user.exception.UserNotFoundException;
import com.PickOne.user.model.domain.user.User;
import com.PickOne.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("사용자 등록 성공")
    void registerUserSuccess() throws Exception {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test@example.com",
                "Password1!"
        );

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmailValue()).thenReturn("test@example.com");

        when(userService.register(anyString(), anyString())).thenReturn(mockUser);

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService).register(request.email(), request.password());
    }

    @Test
    @DisplayName("유효하지 않은 이메일로 사용자 등록 시 실패")
    void registerUserWithInvalidEmail() throws Exception {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "invalid-email",
                "Password1!"
        );

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(anyString(), anyString());
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호로 사용자 등록 시 실패")
    void registerUserWithInvalidPassword() throws Exception {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test@example.com",
                "weak"
        );

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(anyString(), anyString());
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 사용자 등록 시 실패")
    void registerUserWithExistingEmail() throws Exception {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "existing@example.com",
                "Password1!"
        );

        when(userService.register(anyString(), anyString()))
                .thenThrow(new EmailAlreadyExistsException("existing@example.com"));

        // when & then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());

        verify(userService).register(request.email(), request.password());
    }

    @Test
    @DisplayName("ID로 사용자 조회 성공")
    void findUserByIdSuccess() throws Exception {
        // given
        Long userId = 1L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getEmailValue()).thenReturn("test@example.com");

        when(userService.findById(userId)).thenReturn(mockUser);

        // when & then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 조회 시 실패")
    void findUserByNonExistingId() throws Exception {
        // given
        Long userId = 999L;

        when(userService.findById(userId)).thenThrow(new UserNotFoundException(userId));

        // when & then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        verify(userService).findById(userId);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void findUserByEmailSuccess() throws Exception {
        // given
        String email = "test@example.com";

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmailValue()).thenReturn(email);

        when(userService.findByEmail(email)).thenReturn(mockUser);

        // when & then
        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is(email)));

        verify(userService).findByEmail(email);
    }

    @Test
    @DisplayName("모든 사용자 조회 성공")
    void findAllUsersSuccess() throws Exception {
        // given
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getId()).thenReturn(1L);
        when(user1.getEmailValue()).thenReturn("user1@example.com");
        when(user2.getId()).thenReturn(2L);
        when(user2.getEmailValue()).thenReturn("user2@example.com");

        List<User> users = Arrays.asList(user1, user2);
        when(userService.findAll()).thenReturn(users);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("user1@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].email", is("user2@example.com")));

        verify(userService).findAll();
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePasswordSuccess() throws Exception {
        // given
        Long userId = 1L;
        PasswordChangeRequest request = new PasswordChangeRequest(
                "CurrentPass1!",
                "NewPassword1!"
        );

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getEmailValue()).thenReturn("test@example.com");

        when(userService.changePassword(userId, request.currentPassword(), request.newPassword()))
                .thenReturn(mockUser);

        // when & then
        mockMvc.perform(patch("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService).changePassword(userId, request.currentPassword(), request.newPassword());
    }

    @Test
    @DisplayName("잘못된 현재 비밀번호로 비밀번호 변경 시 실패")
    void changePasswordWithWrongCurrentPassword() throws Exception {
        // given
        Long userId = 1L;
        PasswordChangeRequest request = new PasswordChangeRequest(
                "WrongPass1!",
                "NewPassword1!"
        );

        when(userService.changePassword(eq(userId), anyString(), anyString()))
                .thenThrow(new PasswordMismatchException());

        // when & then
        mockMvc.perform(patch("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

        verify(userService).changePassword(userId, request.currentPassword(), request.newPassword());
    }

    @Test
    @DisplayName("유효하지 않은 새 비밀번호로 비밀번호 변경 시 실패")
    void changePasswordWithInvalidNewPassword() throws Exception {
        // given
        Long userId = 1L;
        PasswordChangeRequest request = new PasswordChangeRequest(
                "CurrentPass1!",
                "weak"
        );

        // when & then
        mockMvc.perform(patch("/api/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changePassword(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUserSuccess() throws Exception {
        // given
        Long userId = 1L;

        doNothing().when(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).delete(userId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 삭제 시 실패")
    void deleteNonExistingUser() throws Exception {
        // given
        Long userId = 999L;

        doThrow(new UserNotFoundException(userId)).when(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());

        verify(userService).delete(userId);
    }
}