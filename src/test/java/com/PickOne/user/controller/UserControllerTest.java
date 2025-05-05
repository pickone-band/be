package com.PickOne.user.controller;

import com.PickOne.user.dto.UserResponse;
import com.PickOne.user.model.domain.Email;
import com.PickOne.user.model.domain.Password;
import com.PickOne.user.model.domain.User;
import com.PickOne.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private final Long userId = 1L;
    private final String userEmail = "test@example.com";
    private final String userPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        testUser = User.of(userId, Email.of(userEmail), Password.ofEncoded(userPassword));
    }

    @Test
    @DisplayName("ID로 사용자 조회 성공 테스트")
    void getUserById_Success() {
        // given
        when(userService.findById(userId)).thenReturn(testUser);

        // when
        ResponseEntity<UserResponse> response = userController.getUserById(userId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(userId);
        assertThat(response.getBody().email()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공 테스트")
    void getUserByEmail_Success() {
        // given
        when(userService.findByEmail(userEmail)).thenReturn(testUser);

        // when
        ResponseEntity<UserResponse> response = userController.getUserByEmail(userEmail);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(userId);
        assertThat(response.getBody().email()).isEqualTo(userEmail);
    }
}