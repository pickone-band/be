package com.PickOne.user.model.domain;

import com.PickOne.domain.user.model.domain.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {

    @Test
    @DisplayName("유효한 비밀번호 생성 테스트")
    void of_ValidPassword() {
        // given
        String validPassword = "ValidPassword123!";

        // when
        Password password = Password.of(validPassword);

        // then
        assertThat(password).isNotNull();
        assertThat(password.getValue()).isEqualTo(validPassword);
    }

    @Test
    @DisplayName("인코딩된 비밀번호 생성 테스트")
    void ofEncoded_ValidEncodedPassword() {
        // given
        String encodedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz123456";

        // when
        Password password = Password.ofEncoded(encodedPassword);

        // then
        assertThat(password).isNotNull();
        assertThat(password.getValue()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("널 비밀번호 생성 시 예외 발생 테스트")
    void of_NullPassword() {
        // when & then
        assertThatThrownBy(() -> Password.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 빈 값일 수 없습니다");
    }

    @Test
    @DisplayName("빈 비밀번호 생성 시 예외 발생 테스트")
    void of_EmptyPassword() {
        // when & then
        assertThatThrownBy(() -> Password.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 빈 값일 수 없습니다");
    }

    @Test
    @DisplayName("널 인코딩 비밀번호 생성 시 예외 발생 테스트")
    void ofEncoded_NullPassword() {
        // when & then
        assertThatThrownBy(() -> Password.ofEncoded(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("인코딩된 비밀번호는 빈 값일 수 없습니다");
    }

    @Test
    @DisplayName("빈 인코딩 비밀번호 생성 시 예외 발생 테스트")
    void ofEncoded_EmptyPassword() {
        // when & then
        assertThatThrownBy(() -> Password.ofEncoded(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("인코딩된 비밀번호는 빈 값일 수 없습니다");
    }
}