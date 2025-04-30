package com.PickOne.security.repository;

import com.PickOne.security.model.entity.RoleEntity;
import com.PickOne.security.model.entity.UserRoleEntity;
import com.PickOne.user.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findByUserAndActiveTrue(UserEntity user);
    boolean existsByUserAndRoleAndActiveTrue(UserEntity user, RoleEntity role);
    void deleteByUserAndRole(UserEntity user, RoleEntity role);
}