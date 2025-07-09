package com.pickone.global.oauth2.entity;

import com.pickone.global.common.entity.BaseEntity;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_connections",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"provider", "providerUserId"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
public class UserConnection extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OAuth2Provider provider;

  @Column(nullable = false)
  private String providerUserId;

  @Column(nullable = false)
  private String email;

  private String nickname;

  private String accessToken;

  private String refreshToken;

  @Column(nullable = false)
  private Long userId;

  public void updateTokens(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public void updateAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public static UserConnection create(
      OAuth2Provider provider,
      Long userId,
      String providerUserId,
      String accessToken
  ) {
    return UserConnection.builder()
        .provider(provider)
        .userId(userId)
        .providerUserId(providerUserId)
        .accessToken(accessToken)
        .build();
  }

  public static UserConnection ofFull(
      OAuth2Provider provider,
      String providerUserId,
      String email,
      String nickname,
      String accessToken,
      String refreshToken,
      Long userId
  ) {
    return UserConnection.builder()
        .provider(provider)
        .providerUserId(providerUserId)
        .email(email)
        .nickname(nickname)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .userId(userId)
        .build();
  }
}
