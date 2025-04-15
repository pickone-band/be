package com.PickOne.user.model.domain;

import com.PickOne.user.model.domain.user.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    @DisplayName("유효한 이메일 주소로 Email 객체 생성 성공")
    void createValidEmail() {
        // given
        String validEmail = "test@example.com";

        // when
        Email email = Email.of(validEmail);

        // then
        assertEquals(validEmail, email.getValue());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("null 또는 빈 문자열로 Email 객체 생성 시 예외 발생")
    void createEmailWithNullOrEmpty(String invalidEmail) {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> Email.of(invalidEmail));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "plainaddress",
            "@missingusername.com",
            "username@.com",
            "username@domain",
            "username@domain..com"
    })
    @DisplayName("형식이 잘못된 이메일 주소로 Email 객체 생성 시 예외 발생")
    void createEmailWithInvalidFormat(String invalidEmail) {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> Email.of(invalidEmail));
    }

    @Test
    @DisplayName("동일한 이메일 값을 가진 Email 객체는 equals 비교 시 동등함")
    void equalEmailObjects() {
        // given
        String emailStr = "same@example.com";

        // when
        Email email1 = Email.of(emailStr);
        Email email2 = Email.of(emailStr);

        // then
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    @DisplayName("다른 이메일 값을 가진 Email 객체는 equals 비교 시 다름")
    void unequalEmailObjects() {
        // given
        String emailStr1 = "email1@example.com";
        String emailStr2 = "email2@example.com";

        // when
        Email email1 = Email.of(emailStr1);
        Email email2 = Email.of(emailStr2);

        // then
        assertNotEquals(email1, email2);
    }
}