package com.PickOne.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    // 생성 시간 (수정 불가)
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // 최종 수정 시간
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 생성자 ID
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    // 최종 수정자 ID
    @LastModifiedBy
    private String updatedBy;

//    // 엔티티 상태
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private EntityStatus status = EntityStatus.ACTIVE;

//    // 상태 변경 메서드들
//    public void activate() {
//        this.status = EntityStatus.ACTIVE;
//    }
//
//    public void inactivate() {
//        this.status = EntityStatus.INACTIVE;
//    }
//
//    public void markDeleted() {
//        this.status = EntityStatus.DELETED;
//    }
//
//    // 현재 상태 확인 메서드
//    public boolean isActive() {
//        return this.status == EntityStatus.ACTIVE;
//    }
//
//    public boolean isInactive() {
//        return this.status == EntityStatus.INACTIVE;
//    }
//
//    public boolean isDeleted() {
//        return this.status == EntityStatus.DELETED;
//    }
}
