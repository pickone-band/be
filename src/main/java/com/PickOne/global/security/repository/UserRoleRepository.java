package com.PickOne.global.security.repository;

import com.PickOne.global.security.model.entity.RoleEntity;
import com.PickOne.global.security.model.entity.UserRoleEntity;
import com.PickOne.domain.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findByUserAndActiveTrue(UserEntity user);
    boolean existsByUserAndRoleAndActiveTrue(UserEntity user, RoleEntity role);
    void deleteByUserAndRole(UserEntity user, RoleEntity role);
}