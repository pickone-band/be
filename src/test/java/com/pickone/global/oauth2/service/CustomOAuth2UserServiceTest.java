package com.pickone.global.oauth2.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.service.UserCommandService;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.security.model.entity.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

  @InjectMocks
  private CustomOAuth2UserService sut;

  @Mock private UserCommandService userCommandService;
  @Mock private UserJpaRepository userRepository;
  @Mock private OAuth2User mockOAuth2User;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sut = Mockito.spy(sut);
  }

  @Test
  @DisplayName("loadUser: 기존 유저가 존재하면 해당 유저 반환")
  void loadUser_existingUser() {
    OAuth2UserRequest userRequest = mockOAuth2UserRequest("spotify");
    when(mockOAuth2User.getAttributes()).thenReturn(spotifyAttributes());
    doReturn(mockOAuth2User).when(sut).loadOAuth2User(any());

    UserEntity existingUser = UserFixture.createOAuth2User("user@example.com", "기존유저");
    when(userRepository.findByProfileEmail("user@example.com")).thenReturn(Optional.of(existingUser));

    UserPrincipal result = (UserPrincipal) sut.loadUser(userRequest);

    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("user@example.com");
  }

  @Test
  @DisplayName("loadUser: 유저가 없으면 회원가입 후 반환")
  void loadUser_newUser_signup() {
    OAuth2UserRequest userRequest = mockOAuth2UserRequest("spotify");
    when(mockOAuth2User.getAttributes()).thenReturn(spotifyAttributes());
    doReturn(mockOAuth2User).when(sut).loadOAuth2User(any());

    when(userRepository.findByProfileEmail("user@example.com")).thenReturn(Optional.empty());

    UserEntity newUser = UserFixture.createOAuth2User("user@example.com", "신규유저");
    when(userCommandService.signupWithOAuth2(any(OAuth2UserInfo.class))).thenReturn(newUser);

    UserPrincipal result = (UserPrincipal) sut.loadUser(userRequest);

    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("user@example.com");
  }

  private OAuth2UserRequest mockOAuth2UserRequest(String registrationId) {
    ClientRegistration registration = ClientRegistration.withRegistrationId(registrationId)
        .clientId("mock-client")
        .clientSecret("mock-secret")
        .redirectUri("http://localhost/callback")
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .authorizationUri("https://auth")
        .tokenUri("https://token")
        .userInfoUri("https://userinfo")
        .userNameAttributeName("id")
        .clientName("MockClient")
        .scope("email", "profile")
        .build();

    OAuth2AccessToken token = new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        "access-token",
        Instant.now(),
        Instant.now().plusSeconds(3600)
    );

    return new OAuth2UserRequest(registration, token);
  }

  private Map<String, Object> spotifyAttributes() {
    Map<String, Object> emailMap = new HashMap<>();
    emailMap.put("value", "user@example.com");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("id", "spotify123");
    attributes.put("display_name", "스포티유저");
    attributes.put("emails", List.of(emailMap));
    return attributes;
  }
}