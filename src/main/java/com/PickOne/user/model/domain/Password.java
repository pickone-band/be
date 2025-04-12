package com.PickOne.user.model.domain;

import com.PickOne.user.service.PasswordEncoder;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    private String value;

    private Password(String password) {
        this.value = password;
    }

    public static Password of(String password) {
        validatePassword(password);
        return new Password(password);
    }

    public static Password ofEncoded(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("인코딩된 비밀번호는 빈 값일 수 없습니다.");
        }
        return new Password(encodedPassword);
    }

    private static void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("비밀번호는 null일 수 없습니다.");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            StringBuilder errorMsg = new StringBuilder("비밀번호는 다음 조건을 충족해야 합니다: ");

            if (!Pattern.compile(".*[a-z].*").matcher(password).matches()) {
                errorMsg.append("소문자 포함, ");
            }
            if (!Pattern.compile(".*[A-Z].*").matcher(password).matches()) {
                errorMsg.append("대문자 포함, ");
            }
            if (!Pattern.compile(".*\\d.*").matcher(password).matches()) {
                errorMsg.append("숫자 포함, ");
            }
            if (!Pattern.compile(".*[@$!%*?&].*").matcher(password).matches()) {
                errorMsg.append("특수문자(@$!%*?&) 포함, ");
            }

            throw new IllegalArgumentException(errorMsg.substring(0, errorMsg.length() - 2));
        }
    }

    // 비밀번호 비교 (일반)
    public boolean matches(Password otherPassword) {
        return this.value.equals(otherPassword.value);
    }

    // 암호화된 비밀번호와 평문 비밀번호 비교 (인코더 활용)
    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.value);
    }

    @Override
    public String toString() {
        return "******";
    }
}