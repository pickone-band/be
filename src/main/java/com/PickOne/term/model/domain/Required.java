package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Required {

    private boolean value;

    private Required(boolean value) {
        this.value = value;
    }

    public static Required of(boolean value) {
        return new Required(value);
    }

}