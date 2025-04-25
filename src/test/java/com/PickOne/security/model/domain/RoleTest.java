package com.PickOne.security.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    @Test
    @DisplayName("of 메서드: ID, 이름, 설명으로 역할 객체를 생성한다")
    void of_ShouldCreateRoleWithIdNameAndDescription() {
        // given
        Long id = 1L;
        String name = "ADMIN";
        String description = "관리자 역할";

        // when
        Role role = Role.of(id, name, description);

        // then
        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getName()).isEqualTo(name);
        assertThat(role.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("create 메서드: ID가 null인 새 역할 객체를 생성한다")
    void create_ShouldCreateRoleWithNullIdAndNameAndDescription() {
        // given
        String name = "USER";
        String description = "일반 사용자 역할";

        // when
        Role role = Role.create(name, description);

        // then
        assertThat(role.getId()).isNull();
        assertThat(role.getName()).isEqualTo(name);
        assertThat(role.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("유효하지 않은 역할 이름으로 역할 생성 시 예외가 발생한다")
    void createRole_ShouldThrowException_WhenNameIsInvalid() {
        // given
        Long id = 1L;
        String invalidName = "GUEST";
        String description = "유효하지 않은 역할";

        // when & then
        assertThatThrownBy(() -> Role.of(id, invalidName, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 역할 이름입니다: " + invalidName);
    }

    @Test
    @DisplayName("역할 이름이 null이면 예외가 발생한다")
    void createRole_ShouldThrowException_WhenNameIsNull() {
        // given
        String nullName = null;
        String description = "이름이 null인 역할";

        // when & then
        assertThatThrownBy(() -> Role.create(nullName, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 역할 이름입니다: " + nullName);
    }

    @Test
    @DisplayName("빈 문자열은 유효하지 않은 역할 이름으로 예외 발생")
    void createRole_ShouldThrowException_WhenNameIsEmpty() {
        // given
        String emptyName = "";
        String description = "이름이 빈 문자열인 역할";

        // when & then
        assertThatThrownBy(() -> Role.of(1L, emptyName, description))
                .isInstanceOf(IllegalArgumentException.class);
    }
}