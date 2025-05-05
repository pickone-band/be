package com.PickOne.security.service;

import com.PickOne.security.model.domain.Permission;
import com.PickOne.security.model.domain.PermissionCode;
import com.PickOne.security.model.domain.Role;
import com.PickOne.security.model.entity.PermissionEntity;
import com.PickOne.security.model.entity.RoleEntity;
import com.PickOne.security.model.entity.RolePermissionEntity;
import com.PickOne.security.repository.PermissionRepository;
import com.PickOne.security.repository.RolePermissionRepository;
import com.PickOne.security.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Transactional(readOnly = true)
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("역할을 찾을 수 없습니다: " + name))
                .toDomain();
    }

    @Transactional(readOnly = true)
    public Permission findPermissionByCode(PermissionCode code) {
        return permissionRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("권한을 찾을 수 없습니다: " + code))
                .toDomain();
    }

    @Transactional(readOnly = true)
    public Set<Permission> findPermissionsByRole(String roleName) {
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("역할을 찾을 수 없습니다: " + roleName));

        return rolePermissionRepository.findByRoleAndActiveTrue(role).stream()
                .map(rp -> rp.getPermission().toDomain())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void assignPermissionToRole(String roleName, PermissionCode permissionCode, Long assignedBy) {
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("역할을 찾을 수 없습니다: " + roleName));

        PermissionEntity permission = permissionRepository.findByCode(permissionCode)
                .orElseThrow(() -> new EntityNotFoundException("권한을 찾을 수 없습니다: " + permissionCode));

        // 이미 할당되어 있는지 확인
        if (rolePermissionRepository.existsByRoleAndPermissionAndActiveTrue(role, permission)) {
            return; // 이미 할당된 경우 아무것도 하지 않음
        }

        // 새로운 권한 할당
        RolePermissionEntity rolePermission = new RolePermissionEntity(
                role, permission, LocalDateTime.now(), assignedBy);

        rolePermissionRepository.save(rolePermission);
    }

    @Transactional
    public void revokePermissionFromRole(String roleName, PermissionCode permissionCode) {
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("역할을 찾을 수 없습니다: " + roleName));

        PermissionEntity permission = permissionRepository.findByCode(permissionCode)
                .orElseThrow(() -> new EntityNotFoundException("권한을 찾을 수 없습니다: " + permissionCode));

        // 할당된 권한이 있는지 확인
        if (!rolePermissionRepository.existsByRoleAndPermissionAndActiveTrue(role, permission)) {
            return; // 할당된 권한이 없는 경우 아무것도 하지 않음
        }

        // 권한 비활성화 또는 삭제
        rolePermissionRepository.deleteByRoleAndPermission(role, permission);
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(PermissionEntity::toDomain)
                .collect(Collectors.toList());
    }
}