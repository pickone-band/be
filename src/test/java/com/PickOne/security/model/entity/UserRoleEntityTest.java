package com.PickOne.security.model.entity;

import com.PickOne.security.model.domain.Role;
import com.PickOne.user.model.domain.user.Email;
import com.PickOne.user.model.domain.user.Password;
import com.PickOne.user.model.domain.user.User;
import com.PickOne.user.model.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleEntityTest {

    @Test
    @DisplayName("사용자와 역할 간의 관계를 생성한다")
    void constructor_ShouldCreateUserRoleRelationship() {
        // given
        UserEntity userEntity = createUserEntity();
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        LocalDateTime assignedAt = LocalDateTime.now();
        Long assignedBy = 1L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        // when
        UserRoleEntity userRole = new UserRoleEntity(
                userEntity, roleEntity, assignedAt, assignedBy, expiresAt);

        // then
        assertThat(userRole.getUser()).isEqualTo(userEntity);
        assertThat(userRole.getRole()).isEqualTo(roleEntity);
        assertThat(userRole.getAssignedAt()).isEqualTo(assignedAt);
        assertThat(userRole.getAssignedBy()).isEqualTo(assignedBy);
        assertThat(userRole.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(userRole.isActive()).isTrue();
    }

    @Test
    @DisplayName("사용자-역할 관계를 비활성화한다")
    void deactivate_ShouldSetActiveToFalse() {
        // given
        UserEntity userEntity = createUserEntity();
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "USER", "일반 사용자 역할"));
        LocalDateTime assignedAt = LocalDateTime.now();
        Long assignedBy = 1L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        UserRoleEntity userRole = new UserRoleEntity(
                userEntity, roleEntity, assignedAt, assignedBy, expiresAt);

        assertThat(userRole.isActive()).isTrue();

        // when
        userRole.deactivate();

        // then
        assertThat(userRole.isActive()).isFalse();
    }

    @Test
    @DisplayName("만료일이 지난 사용자-역할 관계는 만료 상태로 인식된다")
    void isExpired_ShouldReturnTrueWhenExpiresAtIsPast() {
        // given
        UserEntity userEntity = createUserEntity();
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "USER", "일반 사용자 역할"));
        LocalDateTime assignedAt = LocalDateTime.now().minusDays(60);
        Long assignedBy = 1L;
        LocalDateTime expiresAt = LocalDateTime.now().minusDays(1); // 이미 만료됨

        // when
        UserRoleEntity userRole = new UserRoleEntity(
                userEntity, roleEntity, assignedAt, assignedBy, expiresAt);

        // then
        assertThat(userRole.isExpired()).isTrue();
    }

    @Test
    @DisplayName("만료일이 아직 지나지 않은 사용자-역할 관계는 유효 상태로 인식된다")
    void isExpired_ShouldReturnFalseWhenExpiresAtIsFuture() {
        // given
        UserEntity userEntity = createUserEntity();
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "USER", "일반 사용자 역할"));
        LocalDateTime assignedAt = LocalDateTime.now().minusDays(1);
        Long assignedBy = 1L;
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30); // 아직 만료되지 않음

        // when
        UserRoleEntity userRole = new UserRoleEntity(
                userEntity, roleEntity, assignedAt, assignedBy, expiresAt);

        // then
        assertThat(userRole.isExpired()).isFalse();
    }

    @Test
    @DisplayName("만료일이 null인 사용자-역할 관계는 만료되지 않는다")
    void isExpired_ShouldReturnFalseWhenExpiresAtIsNull() {
        // given
        UserEntity userEntity = createUserEntity();
        RoleEntity roleEntity = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        LocalDateTime assignedAt = LocalDateTime.now();
        Long assignedBy = 1L;
        LocalDateTime expiresAt = null; // 만료일 없음 (영구적)

        // when
        UserRoleEntity userRole = new UserRoleEntity(
                userEntity, roleEntity, assignedAt, assignedBy, expiresAt);

        // then
        assertThat(userRole.isExpired()).isFalse();
    }

    // 테스트용 UserEntity 생성 헬퍼 메서드
    private UserEntity createUserEntity() {
        User user = User.of(
                1L,
                Email.of("test@example.com"),
                Password.ofEncoded("$2a$10$abcdefghijklmnopqrstuvwxyz012345")
        );
        return UserEntity.from(user);
    }
}