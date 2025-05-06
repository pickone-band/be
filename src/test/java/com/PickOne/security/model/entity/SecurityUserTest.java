package com.PickOne.security.model.entity;

import com.PickOne.global.security.model.domain.Category;
import com.PickOne.global.security.model.domain.Permission;
import com.PickOne.global.security.model.domain.PermissionCode;
import com.PickOne.global.security.model.domain.Role;
import com.PickOne.global.security.model.entity.PermissionEntity;
import com.PickOne.global.security.model.entity.RoleEntity;
import com.PickOne.global.security.model.entity.SecurityUser;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUserTest {

    private UserEntity userEntity;
    private RoleEntity adminRole;
    private RoleEntity userRole;
    private PermissionEntity createPermission;
    private PermissionEntity readPermission;
    private Set<RoleEntity> roles;
    private Set<PermissionEntity> permissions;

    @BeforeEach
    void setUp() {
        // UserEntity 생성
        User user = User.of(
                1L,
                Email.of("admin@example.com"),
                Password.ofEncoded("$2a$10$abcdefghijklmnopqrstuvwxyz012345")
        );
        userEntity = UserEntity.from(user);

        // ID 설정 (리플렉션 사용)
        try {
            java.lang.reflect.Field idField = UserEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(userEntity, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 역할 생성
        adminRole = RoleEntity.from(Role.of(1L, "ADMIN", "관리자 역할"));
        userRole = RoleEntity.from(Role.of(2L, "USER", "일반 사용자 역할"));

        // 권한 생성
        createPermission = PermissionEntity.from(
                Permission.of(1L, PermissionCode.POST_CREATE, Category.POST));
        readPermission = PermissionEntity.from(
                Permission.of(2L, PermissionCode.POST_READ, Category.POST));

        // 역할과 권한 세트 준비
        roles = new HashSet<>();
        roles.add(adminRole);
        roles.add(userRole);

        permissions = new HashSet<>();
        permissions.add(createPermission);
        permissions.add(readPermission);
    }

    @Test
    @DisplayName("SecurityUser는 UserDetails 인터페이스를 올바르게 구현한다")
    void shouldImplementUserDetailsCorrectly() {
        // when
        SecurityUser securityUser = new SecurityUser(userEntity, roles, permissions);

        // then
        assertThat(securityUser.getUsername()).isEqualTo("admin@example.com");
        assertThat(securityUser.getPassword()).isEqualTo("$2a$10$abcdefghijklmnopqrstuvwxyz012345");
        assertThat(securityUser.isAccountNonExpired()).isTrue();
        assertThat(securityUser.isAccountNonLocked()).isTrue();
        assertThat(securityUser.isCredentialsNonExpired()).isTrue();
        assertThat(securityUser.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("SecurityUser는 역할과 권한에 기반한 GrantedAuthority 목록을 제공한다")
    void getAuthorities_ShouldReturnCorrectAuthorities() {
        // when
        SecurityUser securityUser = new SecurityUser(userEntity, roles, permissions);
        Collection<? extends GrantedAuthority> authorities = securityUser.getAuthorities();
        Set<String> authorityNames = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // then
        assertThat(authorities).hasSize(4); // 2개 역할 + 2개 권한
        assertThat(authorityNames).contains(
                "ROLE_ADMIN",
                "ROLE_USER",
                PermissionCode.POST_CREATE.name(),
                PermissionCode.POST_READ.name()
        );
    }

    @Test
    @DisplayName("SecurityUser에서 사용자 ID를 조회할 수 있다")
    void getUserId_ShouldReturnCorrectId() {
        // when
        SecurityUser securityUser = new SecurityUser(userEntity, roles, permissions);

        // then
        assertThat(securityUser.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("SecurityUser는 역할 도메인 객체 목록을 제공한다")
    void getRoleDomains_ShouldReturnCorrectRoleDomains() {
        // when
        SecurityUser securityUser = new SecurityUser(userEntity, roles, permissions);
        Set<Role> roleDomains = securityUser.getRoleDomains();

        // then
        assertThat(roleDomains).hasSize(2);

        Set<String> roleNames = roleDomains.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        assertThat(roleNames).contains("ADMIN", "USER");
    }

    @Test
    @DisplayName("SecurityUser는 권한 도메인 객체 목록을 제공한다")
    void getPermissionDomains_ShouldReturnCorrectPermissionDomains() {
        // when
        SecurityUser securityUser = new SecurityUser(userEntity, roles, permissions);
        Set<Permission> permissionDomains = securityUser.getPermissionDomains();

        // then
        assertThat(permissionDomains).hasSize(2);

        Set<PermissionCode> permissionCodes = permissionDomains.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        assertThat(permissionCodes).contains(PermissionCode.POST_CREATE, PermissionCode.POST_READ);
    }
}