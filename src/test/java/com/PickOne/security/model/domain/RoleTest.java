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
        assertThat(role.getName().getValue()).isEqualTo(name);
        assertThat(role.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("create 메서드: ID가 null인 새 역할 객체를 생성한다")
    void createNew_ShouldCreateRoleWithNullIdAndNameAndDescription() {
        // given
        String name = "USER";
        String description = "일반 사용자 역할";

        // when
        Role role = Role.create(name, description);

        // then
        assertThat(role.getId()).isNull();
        assertThat(role.getName().getValue()).isEqualTo(name);
        assertThat(role.getDescription()).isEqualTo(description);
    }
}