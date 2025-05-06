package com.PickOne.security.service;

import com.PickOne.global.security.model.domain.Role;
import com.PickOne.global.security.model.entity.RoleEntity;
import com.PickOne.global.security.model.entity.UserRoleEntity;
import com.PickOne.global.security.repository.RoleRepository;
import com.PickOne.global.security.repository.UserRoleRepository;
import com.PickOne.global.security.service.UserRoleService;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserRoleServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserRoleService userRoleService;

    @Test
    @DisplayName("사용자 역할 조회 테스트")
    void getUserRoles_Success() {
        // given
        Long userId = 1L;
        UserEntity userEntity = mock(UserEntity.class);

        RoleEntity roleEntity1 = mock(RoleEntity.class);
        Role role1 = mock(Role.class);
        when(roleEntity1.toDomain()).thenReturn(role1);

        RoleEntity roleEntity2 = mock(RoleEntity.class);
        Role role2 = mock(Role.class);
        when(roleEntity2.toDomain()).thenReturn(role2);

        UserRoleEntity userRole1 = mock(UserRoleEntity.class);
        when(userRole1.getRole()).thenReturn(roleEntity1);
        when(userRole1.isExpired()).thenReturn(false);

        UserRoleEntity userRole2 = mock(UserRoleEntity.class);
        when(userRole2.getRole()).thenReturn(roleEntity2);
        when(userRole2.isExpired()).thenReturn(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByUserAndActiveTrue(userEntity)).thenReturn(Arrays.asList(userRole1, userRole2));

        // when
        Set<Role> userRoles = userRoleService.getUserRoles(userId);

        // then
        verify(userRepository).findById(userId);
        verify(userRoleRepository).findByUserAndActiveTrue(userEntity);
        verify(roleEntity1).toDomain();
        verify(roleEntity2).toDomain();

        assertThat(userRoles).hasSize(2);
        assertThat(userRoles).contains(role1, role2);
    }

    @Test
    @DisplayName("사용자 역할 조회 테스트 - 만료된 역할 제외")
    void getUserRoles_ExcludeExpired() {
        // given
        Long userId = 1L;
        UserEntity userEntity = mock(UserEntity.class);

        RoleEntity roleEntity1 = mock(RoleEntity.class);
        Role role1 = mock(Role.class);
        when(roleEntity1.toDomain()).thenReturn(role1);

        RoleEntity roleEntity2 = mock(RoleEntity.class);
        Role role2 = mock(Role.class);
        when(roleEntity2.toDomain()).thenReturn(role2);

        UserRoleEntity userRole1 = mock(UserRoleEntity.class);
        when(userRole1.getRole()).thenReturn(roleEntity1);
        when(userRole1.isExpired()).thenReturn(false);

        UserRoleEntity userRole2 = mock(UserRoleEntity.class);
        when(userRole2.getRole()).thenReturn(roleEntity2);
        when(userRole2.isExpired()).thenReturn(true); // 만료된 역할

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByUserAndActiveTrue(userEntity)).thenReturn(Arrays.asList(userRole1, userRole2));

        // when
        Set<Role> userRoles = userRoleService.getUserRoles(userId);

        // then
        verify(userRepository).findById(userId);
        verify(userRoleRepository).findByUserAndActiveTrue(userEntity);
        verify(roleEntity1).toDomain();

        assertThat(userRoles).hasSize(1);
        assertThat(userRoles).contains(role1);
        assertThat(userRoles).doesNotContain(role2);
    }

    @Test
    @DisplayName("사용자 역할 조회 테스트 - 사용자 없음")
    void getUserRoles_UserNotFound() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userRoleService.getUserRoles(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");

        verify(userRepository).findById(userId);
        verify(userRoleRepository, never()).findByUserAndActiveTrue(any());
    }

    @Test
    @DisplayName("사용자에게 역할 할당 테스트")
    void assignRoleToUser_Success() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_USER";
        Long assignedBy = 999L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        UserEntity userEntity = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(userEntity, roleEntity)).thenReturn(false);
        when(userRoleRepository.save(any(UserRoleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userRoleService.assignRoleToUser(userId, roleName, assignedBy, expiresAt);

        // then
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
        verify(userRoleRepository).existsByUserAndRoleAndActiveTrue(userEntity, roleEntity);
        verify(userRoleRepository).save(any(UserRoleEntity.class));
    }

    @Test
    @DisplayName("사용자에게 역할 할당 테스트 - 이미 할당됨")
    void assignRoleToUser_AlreadyAssigned() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_USER";
        Long assignedBy = 999L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        UserEntity userEntity = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(userEntity, roleEntity)).thenReturn(true);

        // when
        userRoleService.assignRoleToUser(userId, roleName, assignedBy, expiresAt);

        // then
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
        verify(userRoleRepository).existsByUserAndRoleAndActiveTrue(userEntity, roleEntity);
        verify(userRoleRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자에게 역할 할당 테스트 - 사용자 없음")
    void assignRoleToUser_UserNotFound() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_USER";
        Long assignedBy = 999L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userRoleService.assignRoleToUser(userId, roleName, assignedBy, expiresAt))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");

        verify(userRepository).findById(userId);
        verify(roleRepository, never()).findByName(any());
    }

    @Test
    @DisplayName("사용자에게 역할 할당 테스트 - 역할 없음")
    void assignRoleToUser_RoleNotFound() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_NONEXISTENT";
        Long assignedBy = 999L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        UserEntity userEntity = mock(UserEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userRoleService.assignRoleToUser(userId, roleName, assignedBy, expiresAt))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("역할을 찾을 수 없습니다");

        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
    }

    @Test
    @DisplayName("사용자 역할 해제 테스트")
    void revokeRoleFromUser_Success() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_USER";

        UserEntity userEntity = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(userEntity, roleEntity)).thenReturn(true);
        doNothing().when(userRoleRepository).deleteByUserAndRole(userEntity, roleEntity);

        // when
        userRoleService.revokeRoleFromUser(userId, roleName);

        // then
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
        verify(userRoleRepository).existsByUserAndRoleAndActiveTrue(userEntity, roleEntity);
        verify(userRoleRepository).deleteByUserAndRole(userEntity, roleEntity);
    }

    @Test
    @DisplayName("사용자 역할 해제 테스트 - 할당되지 않음")
    void revokeRoleFromUser_NotAssigned() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_USER";

        UserEntity userEntity = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(userEntity, roleEntity)).thenReturn(false);

        // when
        userRoleService.revokeRoleFromUser(userId, roleName);

        // then
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
        verify(userRoleRepository).existsByUserAndRoleAndActiveTrue(userEntity, roleEntity);
        verify(userRoleRepository, never()).deleteByUserAndRole(any(), any());
    }

    @Test
    @DisplayName("임시 역할 할당 테스트")
    void assignTemporaryRoleToUser_Success() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_TEMP";
        Long assignedBy = 999L;
        int durationDays = 7;

        UserEntity userEntity = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(userEntity, roleEntity)).thenReturn(false);
        when(userRoleRepository.save(any(UserRoleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userRoleService.assignTemporaryRoleToUser(userId, roleName, assignedBy, durationDays);

        // then
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
        verify(userRoleRepository).existsByUserAndRoleAndActiveTrue(userEntity, roleEntity);
        verify(userRoleRepository).save(any(UserRoleEntity.class));
    }

    @Test
    @DisplayName("역할 확인 테스트 - 있음")
    void hasRole_True() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_USER";

        UserEntity userEntity = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(userEntity, roleEntity)).thenReturn(true);

        // when
        boolean hasRole = userRoleService.hasRole(userId, roleName);

        // then
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
        verify(userRoleRepository).existsByUserAndRoleAndActiveTrue(userEntity, roleEntity);
        assertThat(hasRole).isTrue();
    }

    @Test
    @DisplayName("역할 확인 테스트 - 없음")
    void hasRole_False() {
        // given
        Long userId = 1L;
        String roleName = "ROLE_ADMIN";

        UserEntity userEntity = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(roleEntity));
        when(userRoleRepository.existsByUserAndRoleAndActiveTrue(userEntity, roleEntity)).thenReturn(false);

        // when
        boolean hasRole = userRoleService.hasRole(userId, roleName);

        // then
        verify(userRepository).findById(userId);
        verify(roleRepository).findByName(roleName);
        verify(userRoleRepository).existsByUserAndRoleAndActiveTrue(userEntity, roleEntity);
        assertThat(hasRole).isFalse();
    }
}