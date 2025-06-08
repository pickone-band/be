package com.PickOne.domain.user.model.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Instrument {

    private String name;

    protected Instrument() {}

    public Instrument(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("악기 이름은 필수입니다.");
        }
        this.name = name;
    }

}