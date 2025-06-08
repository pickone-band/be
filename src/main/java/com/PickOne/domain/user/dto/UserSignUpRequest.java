package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public record UserSignUpRequest(
        String email,
        String rawPassword,
        String nickname,
        String profileImageUrl,
        boolean isPublic,
        boolean isOauth,
        List<String> instruments,
        List<String> genres
) {
    public User toDomain(Long id, PasswordEncoder encoder) {
        return new User(
                id,
                new Email(email),
                Password.ofRaw(rawPassword, encoder),
                new Nickname(nickname),
                new ProfileImage(profileImageUrl),
                isPublic,
                false, // isVerified는 가입 시 false
                isOauth,
                Role.USER,
                instruments.stream().map(Instrument::new).toList(),
                genres.stream().map(Genre::new).toList()
        );
    }
}