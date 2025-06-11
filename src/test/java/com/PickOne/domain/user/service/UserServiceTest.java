package com.PickOne.domain.user.service;

import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserJpaRepository userJpaRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userJpaRepository = mock(UserJpaRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userJpaRepository, null, passwordEncoder);
    }

    @Test
    @DisplayName("ID로 유저 조회 - 성공")
    void findById_success() {
        Long userId = 1L;
        UserEntity entity = createMockUserEntity(userId);
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(entity));

        User result = userService.findById(userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail().getValue()).isEqualTo(entity.getEmail());
    }

    @Test
    @DisplayName("ID로 유저 조회 - 실패")
    void findById_fail() {
        Long userId = 1L;
        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이메일로 유저 조회 - 성공")
    void findByEmail_success() {
        String rawEmail = "test@example.com";
        UserEntity entity = createMockUserEntity(1L);
        when(userJpaRepository.findByEmail(rawEmail)).thenReturn(Optional.of(entity));

        User result = userService.findByEmail(rawEmail);

        assertThat(result.getEmail().getValue()).isEqualTo(rawEmail);
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void updatePassword_success() {
        Long userId = 1L;
        String newRawPassword = "newPass123!";
        String encodedPassword = "encodedPass";
        UserEntity entity = createMockUserEntity(userId);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode(newRawPassword)).thenReturn(encodedPassword);

        userService.updatePassword(userId, newRawPassword);

        assertThat(entity.getPassword().getValue()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateUser_success() {
        Long userId = 1L;
        UserEntity entity = createMockUserEntity(userId);
        User updateData = UserMapper.toDomain(entity).changeNickname(new Nickname("newNick"));

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(entity));

        userService.updateUser(userId, updateData);

        assertThat(entity.getNickname()).isEqualTo("newNick");
    }

    @Test
    @DisplayName("회원 삭제 - 성공")
    void deleteUser_success() {
        Long userId = 1L;
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(createMockUserEntity(userId)));

        userService.deleteUser(userId);

        verify(userJpaRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("회원 삭제 - 실패")
    void deleteUser_fail() {
        Long userId = 1L;
        when(userJpaRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }

    private UserEntity createMockUserEntity(Long id) {
        return new UserEntity(
                "test@example.com",
                Password.ofEncoded("encodedPass"),
                "tester",
                "https://img.test/img.png",
                Role.USER,
                true,
                false,
                List.of(new Instrument("Guitar")),
                List.of(new Genre("Jazz")),
                Gender.MALE,
                LocalDate.of(1990, 1, 1)
        );
    }
}
