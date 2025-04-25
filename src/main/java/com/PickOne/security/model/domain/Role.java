package com.PickOne.security.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Role {

    private static final Set<String> VALID_ROLE_NAMES = Set.of("ADMIN", "USER");

    private Long id;
    private String name;
    private String description;

    private Role(Long id, String name, String description) {
        validateRoleName(name);
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Role of(Long id, String name, String description) {
        return new Role(id, name, description);
    }

    public static Role create(String name, String description) {
        return new Role(null, name, description);
    }

    private static void validateRoleName(String name) {
        if (name == null || !VALID_ROLE_NAMES.contains(name)) {
            throw new IllegalArgumentException("유효하지 않은 역할 이름입니다: " + name);
        }
    }
}