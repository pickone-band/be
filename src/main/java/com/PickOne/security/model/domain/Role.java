package com.PickOne.security.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Role {

    private Long id;
    private RoleName name;
    private String description;

    private Role(Long id, RoleName name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Role of(Long id, String name, String description) {
        return new Role(id, RoleName.of(name), description);
    }

    public static Role createNew(String name, String description) {
        return new Role(null, RoleName.of(name), description);
    }
}