package com.PickOne.security.repository;

import com.PickOne.global.security.model.domain.Category;
import com.PickOne.global.security.model.domain.Permission;
import com.PickOne.global.security.model.domain.PermissionCode;
import com.PickOne.global.security.model.domain.Role;
import com.PickOne.global.security.model.entity.PermissionEntity;
import com.PickOne.global.security.model.entity.RoleEntity;
import com.PickOne.global.security.model.entity.RolePermissionEntity;
import com.PickOne.global.security.repository.RolePermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePermissionRepositoryTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Test
    @DisplayName("역할에 할당된 활성화된 권한을 모두 찾는다")
    void findByRoleAndActiveTrue_ShouldReturnActiveRolePermissions() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long assignedBy = 1L;

        RoleEntity role = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));

        PermissionEntity permission1 = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_CREATE, Category.POST));

        PermissionEntity permission2 = PermissionEntity.from(
                Permission.of(2L, PermissionCode.POST_READ, Category.POST));

        RolePermissionEntity rolePermission1 = new RolePermissionEntity(role, permission1, now, assignedBy);
        RolePermissionEntity rolePermission2 = new RolePermissionEntity(role, permission2, now, assignedBy);

        List<RolePermissionEntity> expectedRolePermissions = Arrays.asList(rolePermission1, rolePermission2);

        when(rolePermissionRepository.findByRoleAndActiveTrue(role)).thenReturn(expectedRolePermissions);

        // when
        List<RolePermissionEntity> foundPermissions = rolePermissionRepository.findByRoleAndActiveTrue(role);

        // then
        assertThat(foundPermissions).hasSize(2);
        assertThat(foundPermissions).extracting(rp -> rp.getPermission().getCode())
                .containsExactlyInAnyOrder(PermissionCode.POST_CREATE, PermissionCode.POST_READ);
        assertThat(foundPermissions).allMatch(RolePermissionEntity::isActive);
    }

    @Test
    @DisplayName("역할과 권한으로 활성화된 관계가 존재하는지 확인한다")
    void existsByRoleAndPermissionAndActiveTrue_ShouldReturnTrue_WhenActiveRelationExists() {
        // given
        RoleEntity role = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        PermissionEntity permission = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_CREATE, Category.POST));

        when(rolePermissionRepository.existsByRoleAndPermissionAndActiveTrue(role, permission)).thenReturn(true);

        // when
        boolean exists = rolePermissionRepository.existsByRoleAndPermissionAndActiveTrue(role, permission);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("역할과 권한 관계를 삭제한다")
    void deleteByRoleAndPermission_ShouldRemoveRelation() {
        // given
        RoleEntity role = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        PermissionEntity permission = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_CREATE, Category.POST));

        // when
        rolePermissionRepository.deleteByRoleAndPermission(role, permission);

        // then
        verify(rolePermissionRepository, times(1)).deleteByRoleAndPermission(role, permission);
    }
}