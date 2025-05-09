package com.PickOne.global.oauth2.model.domain;

import com.PickOne.domain.user.model.domain.Email;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class UserConnection {
    private Long id;
    private Long userId;
    private ProviderId providerId;
    private OAuth2Provider provider;
    private Email email;
    private String name;
    private LocalDateTime lastLogin;

    private UserConnection(Long id, Long userId, ProviderId providerId, OAuth2Provider provider,
                           Email email, String name, LocalDateTime lastLogin) {
        this.id = id;
        this.userId = userId;
        this.providerId = providerId;
        this.provider = provider;
        this.email = email;
        this.name = name;
        this.lastLogin = lastLogin;
    }

    public static UserConnection of(Long id, Long userId, String providerId, OAuth2Provider provider,
                                    String email, String name, LocalDateTime lastLogin) {
        return new UserConnection(
                id,
                userId,
                ProviderId.of(providerId),
                provider,
                Email.of(email),
                name,
                lastLogin
        );
    }

    public static UserConnection create(Long userId, String providerId, OAuth2Provider provider,
                                        String email, String name) {
        return new UserConnection(
                null,
                userId,
                ProviderId.of(providerId),
                provider,
                Email.of(email),
                name,
                LocalDateTime.now()
        );
    }

    public String getProviderId() {
        return providerId.getValue();
    }

    public String getEmailValue() {
        return email.getValue();
    }
}