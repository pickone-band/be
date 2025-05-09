package com.PickOne.global.oauth2.model.domain;

import com.PickOne.domain.user.model.domain.Email;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class OAuth2UserInfo {
    private ProviderId providerId;
    private Email email;
    private String name;
    private OAuth2Provider provider;
    private Map<String, Object> attributes;

    private OAuth2UserInfo(ProviderId providerId, Email email, String name,
                           OAuth2Provider provider, Map<String, Object> attributes) {
        this.providerId = providerId;
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.attributes = attributes;
    }

    public static OAuth2UserInfo of(String providerId, String email, String name,
                                    OAuth2Provider provider, Map<String, Object> attributes) {
        return new OAuth2UserInfo(
                ProviderId.of(providerId),
                Email.of(email),
                name,
                provider,
                attributes
        );
    }

    public String getProviderId() {
        return providerId.getValue();
    }

    public String getEmailValue() {
        return email.getValue();
    }
}