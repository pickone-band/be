package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserUpdateRequestDto;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
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

        UserEntity result = userService.findById(userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo(entity.getEmail());
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
        String email = "test@example.com";
        UserEntity entity = createMockUserEntity(1L);
        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        UserEntity result = userService.findByEmail(email);

        assertThat(result.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void updatePassword_success() {
        Long userId = 1L;
        String rawPassword = "newPass123!";
        String encodedPassword = "encodedPass";
        UserEntity entity = createMockUserEntity(userId);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        userService.updatePassword(userId, rawPassword);

        assertThat(entity.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateUser_success() {
        Long userId = 1L;
        UserEntity entity = createMockUserEntity(userId);
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(entity));

        UserUpdateRequestDto request = new UserUpdateRequestDto(
                "newNick",
                "https://img.new/nick.png",
                true,
                null,
                List.of(Genre.JAZZ), // ✅ 불변 리스트 사용
                List.of()            // ✅ 불변 리스트 사용
        );

        userService.updateUser(userId, request);

        assertThat(entity.getNickname()).isEqualTo("newNick");
        assertThat(entity.getProfileImage()).isEqualTo("https://img.new/nick.png");
        assertThat(entity.getGenres()).containsExactly(Genre.JAZZ);
        assertThat(entity.getUserInstruments()).isEmpty();
    }

    @Test
    @DisplayName("회원 삭제 - 성공")
    void deleteUser_success() {
        Long userId = 1L;
        when(userJpaRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userJpaRepository).deleteById(userId);
    }

    @Test
    @DisplayName("회원 삭제 - 실패")
    void deleteUser_fail() {
        Long userId = 1L;
        when(userJpaRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_INFO_NOT_FOUND.getMessage());
    }

    private UserEntity createMockUserEntity(Long id) {
        UserEntity user = UserEntity.builder()
                .email("test@example.com")
                .password("encodedPass")
                .nickname("tester")
                .profileImage("https://img.test/img.png")
                .role(Role.USER)
                .isPublic(true)
                .isOauth(false)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .mbti(null)
                .genres(List.of(Genre.JAZZ))
                .build();

        if (id != null) {
            // ID 강제 삽입
            try {
                var field = UserEntity.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(user, id);
            } catch (Exception ignored) {}
        }
        return user;
    }
}
