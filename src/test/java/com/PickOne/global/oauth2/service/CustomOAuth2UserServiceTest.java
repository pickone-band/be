package com.PickOne.global.oauth2.service;

import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.domain.OAuth2UserInfo;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import com.PickOne.global.oauth2.repository.UserConnectionRepository;
import com.PickOne.global.security.service.PasswordEncoder;
import com.PickOne.global.security.service.UserRoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomOAuth2UserService oAuth2UserService;

    @Test
    @DisplayName("구글 인증이 아닌 경우 예외 발생")
    void loadUser_NotGoogleProvider() {
        // given
        // Facebook OAuth2UserRequest 생성 (google이 아님)
        ClientRegistration clientRegistration = ClientRegistration
                .withRegistrationId("facebook")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost:8080/login/oauth2/code/facebook")
                .scope("profile", "email")
                .authorizationUri("https://facebook.com/dialog/oauth")
                .tokenUri("https://graph.facebook.com/v13.0/oauth/access_token")
                .userInfoUri("https://graph.facebook.com/v13.0/me")
                .userNameAttributeName("id")
                .clientName("Facebook")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER,
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );

        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);

        // when & then
        // 직접 예외 발생 테스트
        Exception exception = assertThrows(OAuth2AuthenticationException.class, () -> {
            oAuth2UserService.loadUser(userRequest);
        });

        // 예외 메시지 검증
        assertTrue(exception instanceof OAuth2AuthenticationException);
    }

    @Test
    @DisplayName("기존 사용자 연결이 있는 경우 업데이트")
    void processOAuth2User_ExistingConnection() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "12345");
        attributes.put("email", "test@example.com");
        attributes.put("name", "Test User");

        OAuth2UserInfo userInfo = OAuth2UserInfo.of(
                "12345", "test@example.com", "Test User", OAuth2Provider.GOOGLE, attributes
        );

        UserEntity userEntity = mock(UserEntity.class);
        UserConnectionEntity connection = mock(UserConnectionEntity.class);
        User user = mock(User.class);

        when(connection.getUser()).thenReturn(userEntity);
        lenient().when(userEntity.getId()).thenReturn(1L);
        lenient().when(user.getId()).thenReturn(1L);

        when(userConnectionRepository.findByProviderAndProviderId(any(OAuth2Provider.class), eq("12345")))
                .thenReturn(Optional.of(connection));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        User result = oAuth2UserService.processOAuth2User(userInfo);

        // then
        verify(connection).updateConnectionInfo(eq("test@example.com"), eq("Test User"));
        verify(userConnectionRepository).save(connection);
        assertEquals(user, result);
    }

    @Test
    @DisplayName("이메일이 같은 기존 사용자가 있는 경우 연결 생성")
    void processOAuth2User_ExistingUserByEmail() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "12345");
        attributes.put("email", "test@example.com");
        attributes.put("name", "Test User");

        OAuth2UserInfo userInfo = OAuth2UserInfo.of(
                "12345", "test@example.com", "Test User", OAuth2Provider.GOOGLE, attributes
        );

        User user = mock(User.class);
        UserEntity userEntity = mock(UserEntity.class);

        when(user.getId()).thenReturn(1L);

        when(userConnectionRepository.findByProviderAndProviderId(any(OAuth2Provider.class), eq("12345")))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // when
        User result = oAuth2UserService.processOAuth2User(userInfo);

        // then
        verify(userConnectionRepository).save(any(UserConnectionEntity.class));
        assertEquals(user, result);
    }

    @Test
    @DisplayName("신규 사용자 등록")
    void processOAuth2User_RegisterNewUser() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "12345");
        attributes.put("email", "test@example.com");
        attributes.put("name", "Test User");

        OAuth2UserInfo userInfo = OAuth2UserInfo.of(
                "12345", "test@example.com", "Test User", OAuth2Provider.GOOGLE, attributes
        );

        User newUser = mock(User.class);
        UserEntity userEntity = mock(UserEntity.class);

        when(newUser.getId()).thenReturn(1L);

        when(userConnectionRepository.findByProviderAndProviderId(any(OAuth2Provider.class), eq("12345")))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // when
        User result = oAuth2UserService.processOAuth2User(userInfo);

        // then
        verify(userRoleService).assignRoleToUser(eq(1L), eq("USER"), isNull(), isNull());
        verify(userConnectionRepository).save(any(UserConnectionEntity.class));
        assertEquals(newUser, result);
    }
}