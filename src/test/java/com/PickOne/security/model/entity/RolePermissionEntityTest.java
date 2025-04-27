package com.PickOne.security.model.entity;

import com.PickOne.security.model.domain.Category;
import com.PickOne.security.model.domain.Permission;
import com.PickOne.security.model.domain.PermissionCode;
import com.PickOne.security.model.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RolePermissionEntityTest {

    @Test
    @DisplayName("역할과 권한 간의 관계를 생성한다")
    void constructor_ShouldCreateRolePermissionRelationship() {
        // given
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        PermissionEntity permissionEntity = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_CREATE, Category.POST));
        LocalDateTime assignedAt = LocalDateTime.now();
        Long assignedBy = 1L;

        // when
        RolePermissionEntity rolePermission = new RolePermissionEntity(
                roleEntity, permissionEntity, assignedAt, assignedBy);

        // then
        assertThat(rolePermission.getRole()).isEqualTo(roleEntity);
        assertThat(rolePermission.getPermission()).isEqualTo(permissionEntity);
        assertThat(rolePermission.getAssignedAt()).isEqualTo(assignedAt);
        assertThat(rolePermission.getAssignedBy()).isEqualTo(assignedBy);
        assertThat(rolePermission.isActive()).isTrue();
    }

    @Test
    @DisplayName("역할-권한 관계를 비활성화한다")
    void deactivate_ShouldSetActiveToFalse() {
        // given
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        PermissionEntity permissionEntity = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_CREATE, Category.POST));
        LocalDateTime assignedAt = LocalDateTime.now();
        Long assignedBy = 1L;

        RolePermissionEntity rolePermission = new RolePermissionEntity(
                roleEntity, permissionEntity, assignedAt, assignedBy);

        assertThat(rolePermission.isActive()).isTrue();

        // when
        rolePermission.deactivate();

        // then
        assertThat(rolePermission.isActive()).isFalse();
    }

    @Test
    @DisplayName("역할-권한 관계는 생성 시 활성화 상태로 설정된다")
    void constructor_ShouldSetActiveToTrue() {
        // given
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "USER", "일반 사용자 역할"));
        PermissionEntity permissionEntity = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_READ, Category.POST));
        LocalDateTime assignedAt = LocalDateTime.now();
        Long assignedBy = 2L;

        // when
        RolePermissionEntity rolePermission = new RolePermissionEntity(
                roleEntity, permissionEntity, assignedAt, assignedBy);

        // then
        assertThat(rolePermission.isActive()).isTrue();
    }
}