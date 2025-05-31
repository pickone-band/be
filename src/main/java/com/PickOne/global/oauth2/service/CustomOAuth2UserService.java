package com.PickOne.global.oauth2.service;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.domain.OAuth2UserInfo;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import com.PickOne.global.oauth2.repository.UserConnectionRepository;
import com.PickOne.global.security.config.PasswordEncoder;
import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.repository.RefreshTokenRepository;
import com.PickOne.global.security.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserConnectionRepository userConnectionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 오버라이드 가능한 별도 메서드로 분리
     */
    protected OAuth2User loadOAuth2User(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = loadOAuth2User(userRequest);

        String providerName = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Provider provider = OAuth2Provider.from(providerName);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = OAuth2UserInfo.of(provider, attributes);

        Optional<UserConnectionEntity> existingConnection = userConnectionRepository
                .findByProviderAndProviderUserId(provider.name(), userInfo.getId());

        User user;
        if (existingConnection.isPresent()) {
            user = userRepository.findById(existingConnection.get().getUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            String email = userInfo.getEmail();
            Optional<User> maybeUser = userRepository.findByEmail(email);
            if (maybeUser.isPresent()) {
                user = maybeUser.get();
            } else {
                Password password = Password.ofRaw("oauth2TempPass" + email, passwordEncoder);
                user = userRepository.save(new User(null, Email.of(email), password, userInfo.getNickname(), true));
            }

            UserConnectionEntity connection = UserConnectionEntity.builder()
                    .provider(provider)
                    .providerUserId(userInfo.getId())
                    .email(userInfo.getEmail())
                    .nickname(userInfo.getNickname())
                    .userId(user.getId())
                    .build();
            userConnectionRepository.save(connection);
        }

        UserPrincipal userPrincipal = UserPrincipal.from(user);
        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);
        refreshTokenRepository.save(user.getEmail().getValue(), refreshToken, jwtService.getRefreshTokenExpiration());

        return userPrincipal;
    }
}
