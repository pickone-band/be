package com.PickOne.user.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    @DisplayName("유효한 비밀번호로 Password 객체 생성 성공")
    void createValidPassword() {
        // given
        String validPassword = "Password1!";

        // when
        Password password = Password.of(validPassword);

        // then
        assertEquals(validPassword, password.getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null 또는 빈 문자열로 Password 객체 생성 시 예외 발생")
    void createPasswordWithNullOrEmpty(String invalidPassword) {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> Password.of(invalidPassword));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "short1!", // 8자 미만
            "password!", // 대문자 없음
            "PASSWORD1!", // 소문자 없음
            "Password!", // 숫자 없음
            "Password1" // 특수문자 없음
    })
    @DisplayName("형식이 잘못된 비밀번호로 Password 객체 생성 시 예외 발생")
    void createPasswordWithInvalidFormat(String invalidPassword) {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> Password.of(invalidPassword));
    }

    @Test
    @DisplayName("인코딩된 비밀번호로 Password 객체 생성 성공")
    void createPasswordWithEncodedValue() {
        // given
        String encodedPassword = "$2a$10$ABCDEFGHIJKLMNOPQRSTUV";

        // when
        Password password = Password.ofEncoded(encodedPassword);

        // then
        assertEquals(encodedPassword, password.getValue());
    }

    @Test
    @DisplayName("Password 객체 간 일치 여부 확인")
    void testPasswordMatches() {
        // given
        String passwordStr = "Password1!";

        // when
        Password password1 = Password.of(passwordStr);
        Password password2 = Password.of(passwordStr);

        // then
        assertTrue(password1.matches(password2));
    }

    @Test
    @DisplayName("서로 다른 Password 객체는 일치하지 않음")
    void testPasswordDoesNotMatch() {
        // given
        Password password1 = Password.of("Password1!");
        Password password2 = Password.of("DifferentPass1!");

        // when & then
        assertFalse(password1.matches(password2));
    }

    @Test
    @DisplayName("toString 메서드는 비밀번호 값을 마스킹함")
    void testToStringMasksPassword() {
        // given
        Password password = Password.of("SecretPassword1!");

        // when
        String passwordString = password.toString();

        // then
        assertEquals("******", passwordString);
        assertNotEquals("SecretPassword1!", passwordString);
    }
}