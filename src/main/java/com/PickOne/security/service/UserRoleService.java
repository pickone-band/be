package com.PickOne.security.service;

import com.PickOne.security.model.domain.Role;
import com.PickOne.security.model.entity.RoleEntity;
import com.PickOne.security.model.entity.UserRoleEntity;
import com.PickOne.security.repository.RoleRepository;
import com.PickOne.security.repository.UserRoleRepository;
import com.PickOne.user.model.entity.UserEntity;
import com.PickOne.user.repository.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserJpaRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        return userRoleRepository.findByUserAndActiveTrue(user).stream()
                .filter(ur -> !ur.isExpired())
                .map(ur -> ur.getRole().toDomain())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void assignRoleToUser(Long userId, String roleName, Long assignedBy, LocalDateTime expiresAt) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("역할을 찾을 수 없습니다: " + roleName));

        // 이미 할당되어 있는지 확인
        if (userRoleRepository.existsByUserAndRoleAndActiveTrue(user, role)) {
            return; // 이미 할당된 경우 아무것도 하지 않음
        }

        // 새로운 역할 할당
        UserRoleEntity userRole = new UserRoleEntity(
                user, role, LocalDateTime.now(), assignedBy, expiresAt);

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void revokeRoleFromUser(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("역할을 찾을 수 없습니다: " + roleName));

        // 할당된 역할이 있는지 확인
        if (!userRoleRepository.existsByUserAndRoleAndActiveTrue(user, role)) {
            return; // 할당된 역할이 없는 경우 아무것도 하지 않음
        }

        // 역할 비활성화 또는 삭제
        userRoleRepository.deleteByUserAndRole(user, role);
    }

    @Transactional
    public void assignTemporaryRoleToUser(Long userId, String roleName, Long assignedBy, int durationDays) {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(durationDays);
        assignRoleToUser(userId, roleName, assignedBy, expiresAt);
    }

    @Transactional
    public boolean hasRole(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("역할을 찾을 수 없습니다: " + roleName));

        return userRoleRepository.existsByUserAndRoleAndActiveTrue(user, role);
    }
}