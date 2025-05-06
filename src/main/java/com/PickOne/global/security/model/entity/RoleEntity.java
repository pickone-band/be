package com.PickOne.global.security.model.entity;

import com.PickOne.global.common.entity.BaseEntity;
import com.PickOne.global.security.model.domain.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermissionEntity> rolePermissions = new HashSet<>();

    // 정적 팩토리 메서드
    public static RoleEntity from(Role role) {
        RoleEntity entity = new RoleEntity();
        entity.id = role.getId();
        entity.name = role.getName();
        entity.description = role.getDescription();
        return entity;
    }

    // 도메인 객체로 변환
    public Role toDomain() {
        return Role.of(id, name, description);
    }
}