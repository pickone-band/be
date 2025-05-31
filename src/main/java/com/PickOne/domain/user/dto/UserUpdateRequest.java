package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.User;

/**
 * 회원 정보 수정 요청 DTO.
 */
public record UserUpdateRequest(
        String email,
        String nickname,
        boolean isPublic
) {
    public User toDomain(Long id) {
        return new User(
                id,
                Email.of(email),
                null,
                nickname,
                isPublic
        );
    }
}