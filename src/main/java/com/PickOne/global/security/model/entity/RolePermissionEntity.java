package com.PickOne.global.security.model.entity;

import com.PickOne.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"role_id", "permission_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolePermissionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private PermissionEntity permission;

    @Column(nullable = false)
    private LocalDateTime assignedAt;

    @Column
    private Long assignedBy;

    @Column(nullable = false)
    private boolean active = true;

    // 생성자
    public RolePermissionEntity(RoleEntity role, PermissionEntity permission, LocalDateTime assignedAt, Long assignedBy) {
        this.role = role;
        this.permission = permission;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
    }

    // 메서드들
    public void deactivate() {
        this.active = false;
    }
}