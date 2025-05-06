package com.PickOne.security.model.entity;

import com.PickOne.global.security.model.domain.Role;
import com.PickOne.global.security.model.entity.RoleEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleEntityTest {

    @Test
    @DisplayName("도메인 객체로부터 RoleEntity를 생성한다")
    void from_ShouldCreateEntityFromDomain() {
        // given
        Long id = 1L;
        String name = "ADMIN";
        String description = "관리자 역할";

        Role role = Role.of(id, name, description);

        // when
        RoleEntity roleEntity = RoleEntity.from(role);

        // then
        assertThat(roleEntity.getId()).isEqualTo(id);
        assertThat(roleEntity.getName()).isEqualTo(name);
        assertThat(roleEntity.getDescription()).isEqualTo(description);
        assertThat(roleEntity.getRolePermissions()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("RoleEntity로부터 도메인 객체를 생성한다")
    void toDomain_ShouldCreateDomainFromEntity() {
        // given
        Long id = 1L;
        String name = "USER";
        String description = "일반 사용자 역할";

        // RoleEntity 생성 (protected 생성자에 접근하기 위해 from 메서드 사용)
        RoleEntity roleEntity = RoleEntity.from(Role.of(id, name, description));

        // when
        Role role = roleEntity.toDomain();

        // then
        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getName()).isEqualTo(name);
        assertThat(role.getDescription()).isEqualTo(description);
    }
}