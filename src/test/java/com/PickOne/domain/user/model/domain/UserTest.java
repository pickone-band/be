package com.PickOne.domain.user.model.domain;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private final Long userId = 1L;
    private final String userEmail = "test@example.com";
    private final String userPassword = "Password123!";
    private final String encodedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz123456";

    @Test
    @DisplayName("새 사용자 생성 테스트")
    void create_NewUser() {
        // given
        Email email = Email.of(userEmail);
        Password password = Password.of(userPassword);

        // when
        User user = User.create(email, password);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getEmailValue()).isEqualTo(userEmail);
        assertThat(user.getPasswordValue()).isEqualTo(userPassword);
    }

    @Test
    @DisplayName("ID와 함께 사용자 생성 테스트")
    void of_UserWithId() {
        // given
        Email email = Email.of(userEmail);
        Password password = Password.ofEncoded(encodedPassword);

        // when
        User user = User.of(userId, email, password);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(userId);
        assertThat(user.getEmailValue()).isEqualTo(userEmail);
        assertThat(user.getPasswordValue()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("이메일 값 가져오기 테스트")
    void getEmailValue() {
        // given
        User user = User.of(userId, Email.of(userEmail), Password.ofEncoded(encodedPassword));

        // when
        String emailValue = user.getEmailValue();

        // then
        assertThat(emailValue).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("비밀번호 값 가져오기 테스트")
    void getPasswordValue() {
        // given
        User user = User.of(userId, Email.of(userEmail), Password.ofEncoded(encodedPassword));

        // when
        String passwordValue = user.getPasswordValue();

        // then
        assertThat(passwordValue).isEqualTo(encodedPassword);
    }
}