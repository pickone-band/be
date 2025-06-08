package com.PickOne.domain.user.mapper;

import com.PickOne.domain.user.dto.UserResponse;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;

/**
 * UserMapper는 도메인 User와 엔티티 UserEntity,
 * 그리고 API 응답 UserResponse 간의 변환을 담당한다.
 */
public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getProfileImage(),
                entity.isPublic(),
                entity.isVerified(),
                entity.isOauth(),
                entity.getRole(),
                entity.getInstruments(),
                entity.getGenres()
        );
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getProfileImage(),
                user.getRole(),
                user.isPublic(),
                user.isOauth(),
                user.getInstruments(),
                user.getGenres()
        );
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail().getValue(),
                user.getNickname().getValue(),
                user.getProfileImage().getUrl(),
                user.isPublic(),
                user.getRole().name()
        );
    }
}