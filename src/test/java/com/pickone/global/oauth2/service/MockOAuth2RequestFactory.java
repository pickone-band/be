package com.pickone.global.oauth2.service;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.time.Instant;

public class MockOAuth2RequestFactory {

  public static OAuth2UserRequest create(String registrationId) {
    ClientRegistration registration = ClientRegistration.withRegistrationId(registrationId)
        .clientId("mock-client")
        .clientSecret("mock-secret")
        .redirectUri("http://localhost/callback")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationUri("https://auth")
        .tokenUri("https://token")
        .userInfoUri("https://userinfo")
        .userNameAttributeName("email")
        .clientName("MockClient")
        .build();

    OAuth2AccessToken token = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "access-token",
        Instant.now(),
        Instant.now().plusSeconds(3600)
    );

    return new OAuth2UserRequest(registration, token);
  }
}
