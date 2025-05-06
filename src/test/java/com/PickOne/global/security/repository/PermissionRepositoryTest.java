package com.PickOne.global.security.repository;

import com.PickOne.global.security.model.domain.Category;
import com.PickOne.global.security.model.domain.Permission;
import com.PickOne.global.security.model.domain.PermissionCode;
import com.PickOne.global.security.model.entity.PermissionEntity;
import com.PickOne.global.security.repository.PermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionRepositoryTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Test
    @DisplayName("권한을 코드로 찾는다")
    void findByCode_ShouldReturnPermission_WhenPermissionExists() {
        // given
        PermissionCode code = PermissionCode.POST_CREATE;
        PermissionEntity permissionEntity = PermissionEntity.from(
                Permission.of(1L, code, Category.POST));

        when(permissionRepository.findByCode(code)).thenReturn(Optional.of(permissionEntity));

        // when
        Optional<PermissionEntity> foundPermission = permissionRepository.findByCode(code);

        // then
        assertThat(foundPermission).isPresent();
        assertThat(foundPermission.get().getCode()).isEqualTo(code);
        assertThat(foundPermission.get().getCategory()).isEqualTo(Category.POST);
    }

    @Test
    @DisplayName("카테고리별로 권한을 찾는다")
    void findByCategory_ShouldReturnPermissions_WhenCategoryExists() {
        // given
        Category category = Category.POST;

        PermissionEntity permission1 = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_CREATE, category));
        PermissionEntity permission2 = PermissionEntity.from(
                Permission.of(2L, PermissionCode.POST_READ, category));

        List<PermissionEntity> expectedPermissions = Arrays.asList(permission1, permission2);

        when(permissionRepository.findByCategory(category)).thenReturn(expectedPermissions);

        // when
        List<PermissionEntity> foundPermissions = permissionRepository.findByCategory(category);

        // then
        assertThat(foundPermissions).hasSize(2);
        assertThat(foundPermissions).extracting("code")
                .containsExactlyInAnyOrder(PermissionCode.POST_CREATE, PermissionCode.POST_READ);
        assertThat(foundPermissions).extracting("category")
                .containsOnly(Category.POST);
    }

    @Test
    @DisplayName("존재하지 않는 권한 코드로 검색하면 빈 Optional을 반환한다")
    void findByCode_ShouldReturnEmptyOptional_WhenPermissionDoesNotExist() {
        // given
        PermissionCode nonExistentCode = PermissionCode.POST_DELETE;

        when(permissionRepository.findByCode(nonExistentCode)).thenReturn(Optional.empty());

        // when
        Optional<PermissionEntity> foundPermission = permissionRepository.findByCode(nonExistentCode);

        // then
        assertThat(foundPermission).isEmpty();
    }
}