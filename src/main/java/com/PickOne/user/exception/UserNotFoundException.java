package com.PickOne.user.exception;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String email) {
        super("존재하지 않는 사용자입니다: " + email);
    }

    public UserNotFoundException(Long id) {
        super("존재하지 않는 사용자입니다. ID: " + id);
    }
}
