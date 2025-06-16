package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.entity.UserEntity;

/**
 * 회원 정보를 클라이언트에 반환하는 응답 DTO.
 */
public record UserResponseDto(
        Long id,
        String email,
        String nickname,
        String profileImageUrl,
        boolean isPublic,
        String role
) {
    public static UserResponseDto from(UserEntity user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.isPublic(),
                user.getRole().name()
        );
    }
}