package com.PickOne.global.security.model.entity;

import com.PickOne.global.security.model.domain.Category;
import com.PickOne.global.security.model.domain.Permission;
import com.PickOne.global.security.model.domain.PermissionCode;
import com.PickOne.global.security.model.entity.PermissionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionEntityTest {

    @Test
    @DisplayName("도메인 객체로부터 PermissionEntity를 생성한다")
    void from_ShouldCreateEntityFromDomain() {
        // given
        Long id = 1L;
        PermissionCode code = PermissionCode.POST_CREATE;
        Category category = Category.POST;

        Permission permission = Permission.of(id, code, category);

        // when
        PermissionEntity permissionEntity = PermissionEntity.from(permission);

        // then
        assertThat(permissionEntity.getId()).isEqualTo(id);
        assertThat(permissionEntity.getCode()).isEqualTo(code);
        assertThat(permissionEntity.getCategory()).isEqualTo(category);
        assertThat(permissionEntity.getRolePermissions()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("PermissionEntity로부터 도메인 객체를 생성한다")
    void toDomain_ShouldCreateDomainFromEntity() {
        // given
        Long id = 1L;
        PermissionCode code = PermissionCode.POST_READ;
        Category category = Category.POST;

        // PermissionEntity 생성 (protected 생성자에 접근하기 위해 from 메서드 사용)
        PermissionEntity permissionEntity = PermissionEntity.from(Permission.of(id, code, category));

        // when
        Permission permission = permissionEntity.toDomain();

        // then
        assertThat(permission.getId()).isEqualTo(id);
        assertThat(permission.getCode()).isEqualTo(code);
        assertThat(permission.getCategory()).isEqualTo(category);
    }
}