package com.PickOne.user.service;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.user.model.domain.Email;
import com.PickOne.user.model.domain.Password;
import com.PickOne.user.model.domain.User;
import com.PickOne.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

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
    void findById_Success() {
        // given
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // when
        User foundUser = userService.findById(userId);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getEmailValue()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("ID로 사용자 조회 실패 테스트 - 존재하지 않는 사용자")
    void findById_UserNotFound() {
        // given
        Long nonExistentId = 999L;
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(nonExistentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_INFO_NOT_FOUND);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공 테스트")
    void findByEmail_Success() {
        // given
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));

        // when
        User foundUser = userService.findByEmail(userEmail);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getEmailValue()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 실패 테스트 - 존재하지 않는 이메일")
    void findByEmail_UserNotFound() {
        // given
        String nonExistentEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findByEmail(nonExistentEmail))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_INFO_NOT_FOUND);
    }
}