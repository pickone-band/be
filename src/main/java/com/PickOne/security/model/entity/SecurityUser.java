    package com.PickOne.security.model.entity;

    import com.PickOne.security.model.domain.Permission;
    import com.PickOne.security.model.domain.Role;
    import com.PickOne.user.model.domain.User;
    import com.PickOne.user.model.entity.UserEntity;
    import lombok.Getter;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.util.Collection;
    import java.util.HashSet;
    import java.util.Set;
    import java.util.stream.Collectors;

    @Getter
    public class SecurityUser implements UserDetails {

        private final UserEntity user;
        private final Set<RoleEntity> roles;
        private final Set<PermissionEntity> permissions;

        public SecurityUser(UserEntity user, Set<RoleEntity> roles, Set<PermissionEntity> permissions) {
            this.user = user;
            this.roles = roles;
            this.permissions = permissions;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Set<GrantedAuthority> authorities = new HashSet<>();

            // 역할 추가
            for (RoleEntity role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }

            // 권한 추가
            for (PermissionEntity permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission.getCode().name()));
            }

            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // 실제 구현에 맞게 수정
        }

        @Override
        public boolean isAccountNonLocked() {
            return true; // 실제 구현에 맞게 수정
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // 실제 구현에 맞게 수정
        }

        @Override
        public boolean isEnabled() {
            return true; // 실제 구현에 맞게 수정
        }

        // 추가 메서드
        public Long getUserId() {
            return user.getId();
        }

        public User getUserDomain() {
            return user.toDomain();
        }

        public Set<Role> getRoleDomains() {
            return roles.stream()
                    .map(RoleEntity::toDomain)
                    .collect(Collectors.toSet());
        }

        public Set<Permission> getPermissionDomains() {
            return permissions.stream()
                    .map(PermissionEntity::toDomain)
                    .collect(Collectors.toSet());
        }
    }