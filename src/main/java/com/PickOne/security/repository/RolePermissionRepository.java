package com.PickOne.security.repository;

import com.PickOne.security.model.entity.PermissionEntity;
import com.PickOne.security.model.entity.RoleEntity;
import com.PickOne.security.model.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {
    List<RolePermissionEntity> findByRoleAndActiveTrue(RoleEntity role);
    boolean existsByRoleAndPermissionAndActiveTrue(RoleEntity role, PermissionEntity permission);
    void deleteByRoleAndPermission(RoleEntity role, PermissionEntity permission);
}
