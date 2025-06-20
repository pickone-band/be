package com.PickOne.global.oauth2.model.entity;

import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_connections",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"provider", "providerUserId"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserConnectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
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

    public static UserConnectionEntity of(OAuth2Provider provider, String providerUserId,
                                          String email, String nickname, Long userId) {
        return new UserConnectionEntity(null, provider, providerUserId, email, nickname, null, null, userId);
    }

}