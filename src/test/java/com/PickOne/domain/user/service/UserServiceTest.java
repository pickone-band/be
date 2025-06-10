package com.PickOne.domain.user.service;

import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.repository.impl.JpaUserRepositoryImpl;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private JpaUserRepositoryImpl userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(JpaUserRepositoryImpl.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("ID로 유저 조회 - 성공")
    void findById_success() {
        Long userId = 1L;
        User user = createMockUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("ID로 유저 조회 - 실패")
    void findById_fail() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이메일로 유저 조회 - 성공")
    void findByEmail_success() {
        String email = "test@example.com";
        User user = createMockUser(1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);

        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void updatePassword_success() {
        Long userId = 1L;
        String newRawPassword = "newPass123!";
        String encodedPassword = "encodedPass";
        User user = createMockUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newRawPassword)).thenReturn(encodedPassword);

        userService.updatePassword(userId, newRawPassword);

        verify(userRepository, times(1)).update(
                argThat(updated -> updated.getPassword().getValue().equals(encodedPassword))
        );
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateUser_success() {
        Long userId = 1L;
        User current = createMockUser(userId);
        User updated = new User(
                userId,
                current.getEmail(),
                current.getPassword(),
                new Nickname("newNick"),
                current.getProfileImage(),
                false,
                true,
                false,
                current.getRole(),
                List.of(new Instrument("Guitar")),
                List.of(new Genre("Jazz"))
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(current));

        userService.updateUser(userId, updated);

        verify(userRepository, times(1)).update(
                argThat(user -> user.getNickname().getValue().equals("newNick") && !user.isPublic())
        );
    }

    @Test
    @DisplayName("회원 삭제 - 성공")
    void deleteUser_success() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(createMockUser(userId)));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("회원 삭제 - 실패")
    void deleteUser_fail() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }

    // 헬퍼 메서드: 더미 도메인 유저 객체 생성
    private User createMockUser(Long id) {
        return new User(
                id,
                Email.of("test@example.com"),
                Password.ofEncoded("encodedPass"),
                new Nickname("tester"),
                new ProfileImage("https://img.test/img.png"),
                true,
                true,
                false,
                Role.USER,
                List.of(new Instrument("Guitar")),
                List.of(new Genre("Jazz"))
        );
    }
}
