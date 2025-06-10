package com.PickOne.global.security.controller;

import com.PickOne.domain.user.model.domain.*;
import com.PickOne.global.security.dto.AuthResult;
import com.PickOne.global.security.dto.LoginRequest;
import com.PickOne.global.security.dto.RefreshTokenRequest;
import com.PickOne.global.security.dto.SignupRequest;
import com.PickOne.global.security.filter.JwtAuthenticationFilter;
import com.PickOne.global.security.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 API - 성공")
    void signup_success() throws Exception {
        SignupRequest request = new SignupRequest("user@example.com", "pass123", "닉네임");
        User dummyUser = new User(
                1L,
                Email.of(request.email()),
                Password.ofEncoded("encoded"),
                new Nickname(request.nickname()),
                new ProfileImage("https://img.example.com"),
                true,
                false,
                false,
                Role.USER,
                List.of(new Instrument("ELECTRIC_GUITAR")),
                List.of(new Genre("ROCK"))
        );
        AuthResult result = new AuthResult("access-token", "refresh-token", dummyUser);

        Mockito.when(authService.signup(any())).thenReturn(result);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("로그인 API - 성공")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "pass123");
        User dummyUser = new User(
                1L,
                Email.of(request.email()),
                Password.ofEncoded("encoded"),
                new Nickname("닉네임"),
                new ProfileImage("https://img.example.com"),
                true,
                false,
                false,
                Role.USER,
                List.of(new Instrument("ELECTRIC_GUITAR")),
                List.of(new Genre("ROCK"))
        );

        AuthResult result = new AuthResult("access-token", "refresh-token", dummyUser);

        Mockito.when(authService.login(any())).thenReturn(result);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("리프레시 토큰 API - 성공")
    void refresh_success() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh-token-value");
        User dummyUser = new User(
                1L,
                Email.of("refresh@example.com"),
                Password.ofEncoded("encoded"),
                new Nickname("dummy"),
                new ProfileImage("https://img.example.com"),
                true,
                false,
                false,
                Role.USER,
                List.of(new Instrument("ELECTRIC_GUITAR")),
                List.of(new Genre("ROCK"))
        );

        AuthResult result = new AuthResult("access-token", "new-refresh-token", dummyUser);

        Mockito.when(authService.refresh("refresh-token-value")).thenReturn(result);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("로그아웃 API - 성공")
    void logout_success() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .with(csrf())
                        .header("Authorization", "Bearer test-access-token"))
                .andExpect(status().isNoContent());

        Mockito.verify(authService).logout("test-access-token");
    }
}
