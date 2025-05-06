package com.PickOne.global.security.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Permission {

    private Long id;
    private PermissionCode code;
    private Category category;

    private Permission(Long id, PermissionCode code, Category category) {
        this.id = id;
        this.code = code;
        this.category = category;
    }

    /**
     * 기존 권한 객체 생성을 위한 정적 팩토리 메서드
     */
    public static Permission of(Long id, PermissionCode code, Category category) {
        return new Permission(id, code, category);
    }

    /**
     * 새 권한 객체 생성을 위한 정적 팩토리 메서드
     */
    public static Permission create(PermissionCode code, Category category) {
        return new Permission(null, code, category);
    }
}
