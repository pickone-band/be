package com.pickone.global.oauth2.service;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.AuthProvider;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.vo.UserAuthInfo;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;

import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import java.time.LocalDate;
import java.util.List;

public class UserFixture {

  public static UserEntity createOAuth2User(String email, String nickname) {
    return createOAuth2User(email, nickname, OAuth2Provider.SPOTIFY, "default-spotify-id");
  }

  public static UserEntity createOAuth2User(String email, String nickname, OAuth2Provider provider, String providerId) {
    UserEntity user = UserEntity.of(
        email,
        null,
        nickname,
        Gender.MALE,
        LocalDate.of(1995, 1, 1),
        Mbti.ENFP,
        List.of(Genre.POP, Genre.JAZZ)
    );
    injectProvider(user, provider, providerId);
    return user;
  }

  public static UserEntity createPasswordUser(String email, String password) {
    return UserEntity.of(
        email,
        password,
        "기본닉네임",
        Gender.FEMALE,
        LocalDate.of(1992, 6, 15),
        Mbti.ISTJ,
        List.of(Genre.ACOUSTIC)
    );
  }

  public static UserEntity createVerifiedOAuth2User(String email, String nickname, OAuth2Provider provider, String providerId) {
    UserEntity user = createOAuth2User(email, nickname, provider, providerId);
    user.verify();
    return user;
  }

  private static void injectProvider(UserEntity user, OAuth2Provider provider, String providerId) {
    UserAuthInfo original = user.getAuthInfo();
    UserAuthInfo updated = UserAuthInfo.of(
        original.getPassword(),
        AuthProvider.valueOf(provider.name()),
        providerId
    );
    user.changePassword(updated.getPassword());
    user.getAuthInfo().setProvider(updated.getProvider());
    user.getAuthInfo().setProviderId(providerId);
  }
}
