package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.domain.*;

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
                current.getEmail(), // current.getEmail()은 String이므로 다시 객체로 감싸야 함
                current.getPassword(),
                new Nickname(nickname),
                current.getGender(),
                current.getBirthDate(),
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