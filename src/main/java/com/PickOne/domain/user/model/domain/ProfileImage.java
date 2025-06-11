package com.PickOne.domain.user.model.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Embeddable
public class ProfileImage {

    private String url;

    protected ProfileImage() {}

    public ProfileImage(String url) {
        this.url = url;
    }

}