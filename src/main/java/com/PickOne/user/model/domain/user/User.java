package com.PickOne.user.model.domain.user;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")  // ID 기반 동등성 비교
public class User {

    private Long id;
    private Email email;
    private Password password;

    private User(Long id, Email email, Password password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // 신규 사용자 생성 (ID 없음)
    public static User create(Email email, Password password) {
        validateUser(email, password);
        return new User(null, email, password);
    }

    // 기존 사용자 로드 (ID 있음)
    public static User of(Long id, Email email, Password password) {
        validateUser(email, password);
        return new User(id, email, password);
    }

    // 사용자 생성 시 추가 검증 로직
    private static void validateUser(Email email, Password password) {
        if (email == null) {
            throw new IllegalArgumentException("이메일은 null일 수 없습니다.");
        }

        if (password == null) {
            throw new IllegalArgumentException("비밀번호는 null일 수 없습니다.");
        }
    }

    // 비밀번호 변경
    public User changePassword(Password currentPassword, Password newPassword) {
        // 현재 비밀번호 검증
        if (!this.password.matches(currentPassword)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        // 새 비밀번호가 기존 비밀번호와 동일한지 확인
        if (this.password.equals(newPassword)) {
            throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }
        // 새 객체 반환
        this.password = newPassword;

        return this;
    }

    // 편의 메서드
    public String getEmailValue() {
        return this.email.getValue();
    }

    public String getPasswordValue() {
        return this.password.getValue();
    }

    // 신규 사용자 여부 확인
    public boolean isNew() {
        return this.id == null;
    }
}