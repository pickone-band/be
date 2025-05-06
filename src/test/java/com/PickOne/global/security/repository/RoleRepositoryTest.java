package com.PickOne.global.security.repository;

import com.PickOne.global.security.model.domain.Role;
import com.PickOne.global.security.model.entity.RoleEntity;
import com.PickOne.global.security.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    @Test
    @DisplayName("역할을 이름으로 찾는다")
    void findByName_ShouldReturnRole_WhenRoleExists() {
        // given
        String roleName = "ADMIN";
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, roleName, "관리자 역할"));

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));

        // when
        Optional<RoleEntity> foundRole = roleRepository.findByName(roleName);

        // then
        assertThat(foundRole).isPresent();
        assertThat(foundRole.get().getName()).isEqualTo(roleName);
        assertThat(foundRole.get().getDescription()).isEqualTo("관리자 역할");
    }

    @Test
    @DisplayName("존재하지 않는 역할 이름으로 검색하면 빈 Optional을 반환한다")
    void findByName_ShouldReturnEmptyOptional_WhenRoleDoesNotExist() {
        // given
        String nonExistentRoleName = "NON_EXISTENT_ROLE";

        when(roleRepository.findByName(nonExistentRoleName)).thenReturn(Optional.empty());

        // when
        Optional<RoleEntity> foundRole = roleRepository.findByName(nonExistentRoleName);

        // then
        assertThat(foundRole).isEmpty();
    }
}