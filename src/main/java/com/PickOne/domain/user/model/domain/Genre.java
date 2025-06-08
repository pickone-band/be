package com.PickOne.domain.user.model.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Genre {

    private String name;

    protected Genre() {}

    public Genre(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("장르 이름은 필수");
        this.name = name;
    }
}