package com.PickOne.user.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Email validEmail;
    private Password validPassword;

    @BeforeEach
    void setUp() {
        validEmail = Email.of("test@example.com");
        validPassword = Password.of("ValidPass1!");
    }

    @Test
    @DisplayName("유효한 이메일과 비밀번호로 User 객체 생성 성공")
    void createValidUser() {
        // when
        User user = User.create(validEmail, validPassword);

        // then
        assertEquals(validEmail, user.getEmail());
        assertEquals(validPassword, user.getPassword());
        assertNull(user.getId());
        assertTrue(user.isNew());
    }

    @Test
    @DisplayName("ID가 있는 User 객체 생성 성공")
    void createUserWithId() {
        // given
        Long id = 1L;

        // when
        User user = User.of(id, validEmail, validPassword);

        // then
        assertEquals(id, user.getId());
        assertEquals(validEmail, user.getEmail());
        assertEquals(validPassword, user.getPassword());
        assertFalse(user.isNew());
    }

    @Test
    @DisplayName("null 이메일로 User 객체 생성 시 예외 발생")
    void createUserWithNullEmail() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> User.create(null, validPassword));
    }

    @Test
    @DisplayName("null 비밀번호로 User 객체 생성 시 예외 발생")
    void createUserWithNullPassword() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> User.create(validEmail, null));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePasswordSuccess() {
        // given
        User user = User.create(validEmail, validPassword);
        Password newPassword = Password.of("NewPassword1!");

        // when
        User updatedUser = user.changePassword(validPassword, newPassword);

        // then
        assertEquals(newPassword, updatedUser.getPassword());
        assertEquals(validEmail, updatedUser.getEmail());
        assertEquals(user, updatedUser);
    }

    @Test
    @DisplayName("비밀번호 변경 시 현재 비밀번호가 일치하지 않으면 예외 발생")
    void changePasswordWithWrongCurrentPassword() {
        // given
        User user = User.create(validEmail, validPassword);
        Password wrongPassword = Password.of("WrongPass1!");
        Password newPassword = Password.of("NewPassword1!");

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> user.changePassword(wrongPassword, newPassword));
    }

    @Test
    @DisplayName("비밀번호 변경 시 새 비밀번호가 현재 비밀번호와 같으면 예외 발생")
    void changePasswordWithSamePassword() {
        // given
        User user = User.create(validEmail, validPassword);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> user.changePassword(validPassword, validPassword));
    }

    @Test
    @DisplayName("편의 메서드 getEmailValue가 이메일 값을 정확히 반환")
    void getEmailValueReturnsCorrectValue() {
        // given
        User user = User.create(validEmail, validPassword);

        // when
        String emailValue = user.getEmailValue();

        // then
        assertEquals("test@example.com", emailValue);
    }

    @Test
    @DisplayName("편의 메서드 getPasswordValue가 비밀번호 값을 정확히 반환")
    void getPasswordValueReturnsCorrectValue() {
        // given
        User user = User.create(validEmail, validPassword);

        // when
        String passwordValue = user.getPasswordValue();

        // then
        assertEquals("ValidPass1!", passwordValue);
    }

    @Test
    @DisplayName("ID가 같은 User 객체는 equals 비교 시 동등함")
    void equalUserObjects() {
        // given
        Long id = 1L;
        User user1 = User.of(id, validEmail, validPassword);
        User user2 = User.of(id, Email.of("another@example.com"), Password.of("AnotherPass1!"));

        // when & then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("ID가 다른 User 객체는 equals 비교 시 다름")
    void unequalUserObjects() {
        // given
        User user1 = User.of(1L, validEmail, validPassword);
        User user2 = User.of(2L, validEmail, validPassword);

        // when & then
        assertNotEquals(user1, user2);
    }
}