package com.PickOne.domain.user.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class User {
    private Long id;
    private Email email;
    private Password password;
    private boolean verified; // 이메일 인증 여부 필드 추가

    private User(Long id, Email email, Password password, boolean verified) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.verified = verified;
    }

    // 기존 생성자를 업데이트하여 verified 필드 초기화
    private User(Long id, Email email, Password password) {
        this(id, email, password, false); // 기본값은 false
    }

    public static User create(Email email, Password password) {
        return new User(null, email, password);
    }

    public static User of(Long id, Email email, Password password) {
        return new User(id, email, password);
    }

    // 인증 상태를 포함한 생성 메서드 추가
    public static User of(Long id, Email email, Password password, boolean verified) {
        return new User(id, email, password, verified);
    }

    public String getEmailValue() {
        return this.email.getValue();
    }

    public String getPasswordValue() {
        return this.password.getValue();
    }

    public boolean isVerified() {
        return this.verified;
    }

    public void updatePassword(String encodedPassword) {
        this.password = Password.ofEncoded(encodedPassword);
    }

    // 인증 상태 변경을 위한 메서드 추가
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}