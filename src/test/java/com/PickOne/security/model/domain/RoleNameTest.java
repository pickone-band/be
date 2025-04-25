package com.PickOne.security.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RoleNameTest {

    @Test
    @DisplayName("유효한 역할 이름으로 RoleName 객체 생성 성공")
    void createValidRoleName(){
        //given
        String validName = "ADMIN";

        // when
        RoleName roleName = RoleName.of(validName);

        // then
        assertThat(roleName).isNotNull();
        assertThat(roleName.getValue()).isEqualTo(validName);
    }

    @DisplayName("유효하지 않은 역할 이름이면 예외 발생")
    @Test
    void createInvalidRoleName_throwsException() {
        // given
        String invalidName = "GUEST";

        // when & then
        assertThatThrownBy(() -> RoleName.of(invalidName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 역할 이름입니다: " + invalidName);
    }

    @DisplayName("빈 문자열은 유효하지 않은 역할 이름으로 예외 발생")
    @Test
    void emptyRoleName_throwsException() {
        // given
        String emptyName = "";

        // when & then
        assertThatThrownBy(() -> RoleName.of(emptyName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("null은 유효하지 않은 역할 이름으로 예외 발생")
    @Test
    void nullRoleName_throwsException() {
        // given
        String nullName = null;

        // when & then
        assertThatThrownBy(() -> RoleName.of(nullName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}