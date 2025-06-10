package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.domain.Nickname;
import com.PickOne.domain.user.model.domain.ProfileImage;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.domain.Instrument;
import com.PickOne.domain.user.model.domain.Genre;

import java.util.List;

/**
 * 회원 정보 수정 요청 DTO.
 */
public record UserUpdateRequest(
        String nickname,
        String profileImageUrl,
        boolean isPublic,
        List<String> instruments,
        List<String> genres
) {
    public User toUpdatedDomain(User current) {
        return new User(
                current.getId(),
                current.getEmail(),
                current.getPassword(),
                new Nickname(nickname),
                new ProfileImage(profileImageUrl),
                isPublic,
                current.isVerified(),
                current.isOauth(),
                current.getRole(),
                instruments.stream().map(Instrument::new).toList(),
                genres.stream().map(Genre::new).toList()
        );
    }
}