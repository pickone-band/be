package com.pickone.global.oauth2.service;

import com.pickone.global.music.dto.MusicInfo;
import com.pickone.global.music.repository.UserMusicJpaRepository;
import com.pickone.global.music.service.GoogleMusicService;
import com.pickone.global.music.service.SpotifyMusicService;
import com.pickone.domain.user.model.domain.*;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;

import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.oauth2.model.entity.UserConnectionEntity;
import com.pickone.global.oauth2.repository.UserConnectionRepository;
import com.pickone.global.security.model.entity.UserPrincipal;
import com.pickone.global.security.repository.RefreshTokenRepository;
import com.pickone.global.security.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    private UserJpaRepository userJpaRepository;
    private UserConnectionRepository userConnectionRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private RefreshTokenRepository refreshTokenRepository;
    private SpotifyMusicService spotifyMusicService;
    private GoogleMusicService googleMusicService;
    private UserMusicJpaRepository userMusicJpaRepository;
    private CustomOAuth2UserService service;

    @BeforeEach
    void setUp() {
        userJpaRepository = mock(UserJpaRepository.class);
        userConnectionRepository = mock(UserConnectionRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);
        spotifyMusicService = mock(SpotifyMusicService.class);
        googleMusicService = mock(GoogleMusicService.class);
        userMusicJpaRepository = mock(UserMusicJpaRepository.class);

        service = new CustomOAuth2UserService(
                userJpaRepository,
                userConnectionRepository,
                passwordEncoder,
                jwtService,
                refreshTokenRepository,
                spotifyMusicService,
                googleMusicService,
                userMusicJpaRepository
        ) {
            @Override
            protected OAuth2User loadOAuth2User(OAuth2UserRequest userRequest) {
                return new DefaultOAuth2User(
                        List.of(() -> "ROLE_USER"),
                        Map.of("sub", "oauth-sub-id", "email", "test@example.com", "name", "Tester"),
                        "sub"
                );
            }
        };
    }

    @Test
    void loadUser_newUser_createsUserAndConnection() {
        // given
        String providerId = "oauth-sub-id";
        String email = "test@example.com";
        String name = "Tester";

        ClientRegistration registration = MockClientRegistration.google();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "fake-token", null, null
        );
        OAuth2UserRequest userRequest = new OAuth2UserRequest(registration, accessToken);

        OAuth2UserInfo userInfo = mock(OAuth2UserInfo.class);
        when(userInfo.getId()).thenReturn(providerId);
        when(userInfo.getEmail()).thenReturn(email);
        when(userInfo.getNickname()).thenReturn(name);
        when(userInfo.getGender()).thenReturn(Gender.FEMALE);
        when(userInfo.getBirthDate()).thenReturn(LocalDate.of(1993, 5, 15));
        when(userInfo.getProfileImageUrl()).thenReturn("img");

        try (MockedStatic<OAuth2UserInfo> mockedStatic = mockStatic(OAuth2UserInfo.class)) {
            mockedStatic.when(() -> OAuth2UserInfo.of(OAuth2Provider.GOOGLE, Map.of(
                    "sub", providerId,
                    "email", email,
                    "name", name
            ))).thenReturn(userInfo);

            when(userConnectionRepository.findByProviderAndProviderUserId("GOOGLE", providerId))
                    .thenReturn(Optional.empty());
            when(userJpaRepository.findByProfile_Email(email)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(any())).thenReturn("encoded-password");

          when(userJpaRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            return UserEntity.of(
                u.getProfile().getEmail(),
                u.getProfile().getPassword(),
                u.getProfile().getNickname(),
                u.getProfile().getGender(),
                u.getProfile().getBirthDate(), // profile에서 꺼내야 하는 경우, getBirthDate()가 어디에 있는지 확인
                null,      // mbti
                List.of()  // genres
            );
          });

            when(jwtService.generateAccessToken(any())).thenReturn(UUID.randomUUID().toString());
            when(jwtService.generateRefreshToken(any())).thenReturn(UUID.randomUUID().toString());
            when(jwtService.getRefreshTokenExpiration()).thenReturn(10000L);
            when(googleMusicService.getCurrentlyPlaying(anyString()))
                    .thenReturn(new MusicInfo("title", "artist", "album", "img", "url"));

            // when
            OAuth2User result = service.loadUser(userRequest);

            // then
            assertThat(result).isInstanceOf(UserPrincipal.class);
            verify(userConnectionRepository).save(any(UserConnectionEntity.class));
            verify(refreshTokenRepository).save(eq(email), any(String.class), any(Long.class));
            verify(userMusicJpaRepository).save(any());
        }
    }

    static class MockClientRegistration {
        public static ClientRegistration google() {
            return ClientRegistration.withRegistrationId("google")
                    .clientId("test-client-id")
                    .clientSecret("test-secret")
                    .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .scope("email", "profile")
                    .authorizationUri("https://accounts.google.com/o/oauth2/auth")
                    .tokenUri("https://oauth2.googleapis.com/token")
                    .userInfoUri("https://openidconnect.googleapis.com/v1/userinfo")
                    .userNameAttributeName("sub")
                    .clientName("Google")
                    .build();
        }
    }
}
