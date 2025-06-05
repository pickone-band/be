package com.PickOne.domain.user.service;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.security.config.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("ID로 유저를 조회하면 일치하는 User 객체를 반환한다")
    void findById_success() {
        // given
        Long userId = 1L;
        User user = new User(userId, Email.of("test@example.com"), Password.ofEncoded("hashedPass"), "nickname", true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = userService.findById(userId);

        // then
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 유저를 조회하면 예외가 발생한다")
    void findById_fail() {
        // given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("유저 정보를 수정하면 새로운 User 객체가 반환된다")
    void updateUser_success() {
        // given
        Long userId = 1L;
        User existing = new User(userId, Email.of("old@example.com"), Password.ofEncoded("hashedPass"), "oldNick", false);
        User update = new User(null, Email.of("new@example.com"), null, "newNick", true);
        User expected = new User(userId, update.getEmail(), existing.getPassword(), update.getNickname(), update.isPublic());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(expected);

        // when
        User result = userService.updateUser(userId, update);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("유저를 삭제하면 Repository에서 deleteById가 호출된다")
    void deleteUser_success() {
        // given
        Long userId = 1L;

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("비밀번호를 업데이트하면 인코딩된 비밀번호로 저장된다")
    void updatePassword_success() {
        // given
        Long userId = 1L;
        String newRawPassword = "NewPassword123!";
        String encodedPassword = "encoded123";
        User user = new User(userId, Email.of("user@example.com"), Password.ofEncoded("old"), "nick", true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newRawPassword)).thenReturn(encodedPassword);

        // when
        userService.updatePassword(userId, newRawPassword);

        // then
        assertThat(user.getPassword().getValue()).isEqualTo(encodedPassword);
        verify(userRepository).save(user);
    }
}
