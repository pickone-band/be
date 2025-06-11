package com.PickOne.domain.user.mapper;

import com.PickOne.domain.user.dto.UserResponse;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Nickname;
import com.PickOne.domain.user.model.domain.ProfileImage;
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
                new Email(entity.getEmail()), // String → Email
                entity.getPassword(),
                new Nickname(entity.getNickname()),
                entity.getGender(),
                entity.getBirthDate(),
                new ProfileImage(entity.getProfileImage()),
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
                user.getEmail().getValue(),          // Email → String
                user.getPassword(),
                user.getNickname().getValue(),       // Nickname → String
                user.getProfileImage().getUrl(),     // ProfileImage → String
                user.getRole(),
                user.isPublic(),
                user.isOauth(),
                user.getInstruments(),
                user.getGenres(),
                user.getGender(),
                user.getBirthDate()
        );
    }


    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail().getValue(),
                user.getNickname().getValue(),
                user.getProfileImage() != null ? user.getProfileImage().getUrl() : null,
                user.isPublic(),
                user.getRole().name()
        );
    }
}