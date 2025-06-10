package com.PickOne.domain.user.model.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class Nickname {

    private String value;

    protected Nickname() {}

    public Nickname(String value) {
        if (value == null || value.length() < 2 || value.length() > 20) {
            throw new IllegalArgumentException("닉네임은 2~20자여야 합니다.");
        }
        this.value = value;
    }

}