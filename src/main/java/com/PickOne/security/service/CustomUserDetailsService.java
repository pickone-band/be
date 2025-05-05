package com.PickOne.security.service;

import com.PickOne.security.model.entity.PermissionEntity;
import com.PickOne.security.model.entity.RoleEntity;
import com.PickOne.security.model.entity.RolePermissionEntity;
import com.PickOne.security.model.entity.SecurityUser;
import com.PickOne.security.model.entity.UserRoleEntity;
import com.PickOne.security.repository.RolePermissionRepository;
import com.PickOne.security.repository.UserRoleRepository;
import com.PickOne.user.model.entity.UserEntity;
import com.PickOne.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return createSecurityUser(user);
    }

    @Transactional(readOnly = true)
    public SecurityUser loadUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. ID: " + id));

        return createSecurityUser(user);
    }

    private SecurityUser createSecurityUser(UserEntity user) {
        // 사용자의 역할 조회
        List<UserRoleEntity> userRoles = userRoleRepository.findByUserAndActiveTrue(user);

        // 활성화된 역할만 필터링
        Set<RoleEntity> roles = userRoles.stream()
                .filter(ur -> !ur.isExpired())
                .map(UserRoleEntity::getRole)
                .collect(Collectors.toSet());

        // 역할에 할당된 권한 조회
        Set<PermissionEntity> permissions = new HashSet<>();
        for (RoleEntity role : roles) {
            List<RolePermissionEntity> rolePermissions = rolePermissionRepository.findByRoleAndActiveTrue(role);
            rolePermissions.stream()
                    .map(RolePermissionEntity::getPermission)
                    .forEach(permissions::add);
        }

        return new SecurityUser(user, roles, permissions);
    }
}