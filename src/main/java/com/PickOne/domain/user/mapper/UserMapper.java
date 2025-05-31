package com.PickOne.domain.user.mapper;

import com.PickOne.domain.user.dto.UserResponse;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;

/**
 * UserMapper는 도메인 User와 엔티티 UserEntity, 그리고 API 응답 UserResponse 간의 변환을 담당한다.
 * 계정 공개 여부(public) 필드도 모든 계층에서 반영된다.
 */
public class UserMapper {

    public static User toDomain(UserEntity entity) {
        return User.of(
                entity.getId(),
                Email.of(entity.getEmail()),
                Password.ofEncoded(entity.getPassword()),
                entity.getNickname(),
                entity.isPublic(),
                entity.isVerified() // ✅ 추가
        );
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getEmail().getValue(),
                user.getPassword().getValue(),
                user.getNickname(),
                user.isPublic(),
                user.isVerified()
        );
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail().getValue(),
                user.getNickname(),
                user.isPublic()
        );
    }

}
