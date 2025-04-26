package com.PickOne.security.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    @DisplayName("of 메서드: ID, 권한 코드, 카테고리로 권한 객체를 생성한다")
    void of_ShouldCreatePermissionWithIdCodeAndCategory() {
        // given
        Long id = 1L;
        PermissionCode code = PermissionCode.POST_CREATE;
        Category category = Category.POST;

        // when
        Permission permission = Permission.of(id, code, category);

        // then
        assertThat(permission).isNotNull();
        assertThat(permission.getId()).isEqualTo(id);
        assertThat(permission.getCode()).isEqualTo(code);
        assertThat(permission.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("create 메서드: ID가 null인 새 권한 객체를 생성한다")
    void create_ShouldCreatePermissionWithNullIdCodeAndCategory() {
        // given
        PermissionCode code = PermissionCode.POST_READ;
        Category category = Category.POST;

        // when
        Permission permission = Permission.create(code, category);

        // then
        assertThat(permission).isNotNull();
        assertThat(permission.getId()).isNull();
        assertThat(permission.getCode()).isEqualTo(code);
        assertThat(permission.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("권한 코드와 카테고리가 일치하는지 확인한다")
    void shouldHaveMatchingCategoryInPermissionCode() {
        // given
        PermissionCode code = PermissionCode.POST_UPDATE;
        Category category = Category.POST;

        // when
        Permission permission = Permission.create(code, category);

        // then
        assertThat(permission.getCode().getCategory()).isEqualTo(permission.getCategory());
    }

}