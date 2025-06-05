package com.PickOne.domain.user.model.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 도메인 User 객체는 Email, Password VO를 포함하며, nickname과 isPublic은 사용자 프로필 설정을 반영한다.
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class User {
    private final Long id;
    private final Email email;
    private final Password password;
    private final String nickname;
    private boolean isVerified = false;
    private final boolean isPublic;

    private User(Long id, Email email, Password password, String nickname, boolean isPublic, boolean isVerified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.isPublic = isPublic;
        this.isVerified = isVerified;
    }

    public static User of(Long id, Email email, Password password, String nickname, boolean isPublic) {
        return new User(id, email, password, nickname, isPublic, false);
    }

    public static User of(Long id, Email email, Password password, String nickname, boolean isPublic, boolean isVerified) {
        return new User(id, email, password, nickname, isPublic, isVerified);
    }

    public User changePassword(Password newPassword) {
        return new User(id, email, newPassword, nickname, isPublic, isVerified);
    }
    public void verify() {
        this.isVerified = true;
    }
}


