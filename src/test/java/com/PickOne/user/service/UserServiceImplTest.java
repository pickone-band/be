package com.PickOne.user.service;

import com.PickOne.user.exception.EmailAlreadyExistsException;
import com.PickOne.user.exception.PasswordMismatchException;
import com.PickOne.user.exception.UserNotFoundException;
import com.PickOne.user.model.domain.user.Password;
import com.PickOne.user.model.domain.user.User;
import com.PickOne.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("사용자 등록 성공")
    void registerUserSuccess() {
        // given
        String email = "test@example.com";
        String password = "Password1!";
        String encodedPassword = "encoded_password";

        User mockUser = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // when
        User registeredUser = userService.register(email, password);

        // then
        assertNotNull(registeredUser);
        assertEquals(mockUser, registeredUser);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).encode(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 사용자 등록 시 예외 발생")
    void registerUserWithExistingEmail() {
        // given
        String email = "existing@example.com";
        String password = "Password1!";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));

        // when & then
        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(email, password));
        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("ID로 사용자 찾기 성공")
    void findUserByIdSuccess() {
        // given
        Long userId = 1L;
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // when
        User foundUser = userService.findById(userId);

        // then
        assertNotNull(foundUser);
        assertEquals(mockUser, foundUser);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 찾기 시 예외 발생")
    void findUserByNonExistingId() {
        // given
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 성공")
    void findUserByEmailSuccess() {
        // given
        String email = "test@example.com";
        User mockUser = mock(User.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // when
        User foundUser = userService.findByEmail(email);

        // then
        assertNotNull(foundUser);
        assertEquals(mockUser, foundUser);
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자 찾기 시 예외 발생")
    void findUserByNonExistingEmail() {
        // given
        String email = "nonexisting@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.findByEmail(email));
        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("모든 사용자 찾기 성공")
    void findAllUsersSuccess() {
        // given
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        List<User> mockUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        // when
        List<User> foundUsers = userService.findAll();

        // then
        assertEquals(2, foundUsers.size());
        assertEquals(mockUsers, foundUsers);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePasswordSuccess() {
        // given
        Long userId = 1L;
        String currentPassword = "CurrentPass1!";
        String newPassword = "NewPassword1!";
        String encodedCurrentPassword = "encoded_current_password";
        String encodedNewPassword = "encoded_new_password";

        User mockUser = mock(User.class);
        Password mockCurrentPassword = mock(Password.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getPassword()).thenReturn(mockCurrentPassword);
        when(mockUser.getPasswordValue()).thenReturn(encodedCurrentPassword);
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        Password newPasswordObj = Password.ofEncoded(encodedNewPassword);
        when(mockUser.changePassword(mockCurrentPassword, newPasswordObj)).thenReturn(mockUser);

        // when
        User result = userService.changePassword(userId, currentPassword, newPassword);

        // then
        assertNotNull(result);
        assertEquals(mockUser, result);

        // verify
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
        verify(passwordEncoder).encode(newPassword);
        verify(mockUser).changePassword(mockCurrentPassword, newPasswordObj);

    }

    @Test
    @DisplayName("잘못된 현재 비밀번호로 비밀번호 변경 시 예외 발생")
    void changePasswordWithWrongCurrentPassword() {
        // given
        Long userId = 1L;
        String wrongPassword = "WrongPass1!";
        String newPassword = "NewPassword1!";

        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.getPasswordValue()).thenReturn("encoded_current_password");
        when(passwordEncoder.matches(wrongPassword, mockUser.getPasswordValue())).thenReturn(false);

        // when & then
        assertThrows(PasswordMismatchException.class,
                () -> userService.changePassword(userId, wrongPassword, newPassword));

        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(wrongPassword, mockUser.getPasswordValue());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUserSuccess() {
        // given
        Long userId = 1L;
        User mockUser = mock(User.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // when
        userService.delete(userId);

        // then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(mockUser);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 삭제 시 예외 발생")
    void deleteNonExistingUser() {
        // given
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }
}