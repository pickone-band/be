package com.PickOne.user.exception;

public class PasswordMismatchException extends UserException {
    public PasswordMismatchException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
