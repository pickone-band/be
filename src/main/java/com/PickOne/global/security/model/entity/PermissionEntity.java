package com.PickOne.global.security.model.entity;

import com.PickOne.global.common.entity.BaseEntity;
import com.PickOne.global.security.model.domain.Category;
import com.PickOne.global.security.model.domain.Permission;
import com.PickOne.global.security.model.domain.PermissionCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PermissionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PermissionCode code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RolePermissionEntity> rolePermissions = new HashSet<>();

    // 정적 팩토리 메서드
    public static PermissionEntity from(Permission permission) {
        PermissionEntity entity = new PermissionEntity();
        entity.id = permission.getId();
        entity.code = permission.getCode();
        entity.category = permission.getCategory();
        return entity;
    }

    // 도메인 객체로 변환
    public Permission toDomain() {
        return Permission.of(id, code, category);
    }
}