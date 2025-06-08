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
        if (url == null || !url.startsWith("http")) {
            throw new IllegalArgumentException("유효한 프로필 이미지 URL이어야 합니다.");
        }
        this.url = url;
    }

}