package com.PickOne.security.repository;

import com.PickOne.global.security.model.domain.Role;
import com.PickOne.global.security.model.entity.RoleEntity;
import com.PickOne.global.security.model.entity.UserRoleEntity;
import com.PickOne.global.security.repository.UserRoleRepository;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
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
class UserRoleRepositoryTest {

    @Mock
    private UserRoleRepository userRoleRepository;

    @Test
    @DisplayName("사용자에게 할당된 활성화된 역할을 모두 찾는다")
    void findByUserAndActiveTrue_ShouldReturnActiveUserRoles() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long assignedBy = 1L;

        UserEntity user = UserEntity.from(User.of(1L,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded_password")));

        RoleEntity adminRole = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        RoleEntity userRole = RoleEntity.from(Role.of(2L, "USER", "일반 사용자 역할"));

        UserRoleEntity userRole1 = new UserRoleEntity(user, adminRole, now, assignedBy, null);
        UserRoleEntity userRole2 = new UserRoleEntity(user, userRole, now, assignedBy, now.plusDays(30));

        List<UserRoleEntity> expectedUserRoles = Arrays.asList(userRole1, userRole2);

        when(userRoleRepository.findByUserAndActiveTrue(user)).thenReturn(expectedUserRoles);

        // when
        List<UserRoleEntity> foundRoles = userRoleRepository.findByUserAndActiveTrue(user);

        // then
        assertThat(foundRoles).hasSize(2);
        assertThat(foundRoles).extracting(ur -> ur.getRole().getName())
                .containsExactlyInAnyOrder("ADMIN", "USER");
        assertThat(foundRoles).allMatch(UserRoleEntity::isActive);
    }

    @Test
    @DisplayName("사용자와 역할로 활성화된 관계가 존재하는지 확인한다")
    void existsByUserAndRoleAndActiveTrue_ShouldReturnTrue_WhenActiveRelationExists() {
        // given
        UserEntity user = UserEntity.from(User.of(1L,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded_password")));

        RoleEntity role = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));

        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(user, role)).thenReturn(true);

        // when
        boolean exists = userRoleRepository.existsByUserAndRoleAndActiveTrue(user, role);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("사용자와 역할 관계를 삭제한다")
    void deleteByUserAndRole_ShouldRemoveRelation() {
        // given
        UserEntity user = UserEntity.from(User.of(1L,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded_password")));

        RoleEntity role = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));

        // when
        userRoleRepository.deleteByUserAndRole(user, role);

        // then
        verify(userRoleRepository, times(1)).deleteByUserAndRole(user, role);
    }
}