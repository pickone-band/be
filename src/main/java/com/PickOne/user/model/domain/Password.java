// Password.java
package com.PickOne.user.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Password {
    private String value;

    private Password(String password) {
        this.value = password;
    }

    public static Password of(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 빈 값일 수 없습니다.");
        }
        return new Password(password);
    }

    public static Password ofEncoded(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("인코딩된 비밀번호는 빈 값일 수 없습니다.");
        }
        return new Password(encodedPassword);
    }
}