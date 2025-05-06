package com.PickOne.user.model.domain;

import com.PickOne.domain.user.model.domain.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("유효한 이메일 생성 테스트")
    void of_ValidEmail() {
        // given
        String validEmail = "test@example.com";

        // when
        Email email = Email.of(validEmail);

        // then
        assertThat(email).isNotNull();
        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("널 이메일 생성 시 예외 발생 테스트")
    void of_NullEmail() {
        // when & then
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일은 빈 값일 수 없습니다");
    }

    @Test
    @DisplayName("빈 이메일 생성 시 예외 발생 테스트")
    void of_EmptyEmail() {
        // when & then
        assertThatThrownBy(() -> Email.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일은 빈 값일 수 없습니다");
    }
}