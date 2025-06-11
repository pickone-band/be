package com.PickOne.global.oauth2.service;

import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.Role;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.domain.OAuth2UserInfo;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import com.PickOne.global.oauth2.repository.UserConnectionRepository;

import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.repository.RefreshTokenRepository;
import com.PickOne.global.security.service.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserJpaRepository userJpaRepository;
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

        UserEntity user;
        if (existingConnection.isPresent()) {
            user = userJpaRepository.findById(existingConnection.get().getUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            Optional<UserEntity> maybeUser = userJpaRepository.findByEmail(userInfo.getEmail());
            if (maybeUser.isPresent()) {
                user = maybeUser.get();
            } else {
                Password tempPassword = Password.ofRaw("oauth2TempPass" + userInfo.getEmail(), passwordEncoder);
                user = new UserEntity(
                        userInfo.getEmail(),
                        tempPassword,
                        userInfo.getNickname(),
                        userInfo.getProfileImageUrl(),
                        Role.USER,
                        true,
                        true,
                        List.of(),
                        List.of(),
                        userInfo.getGender(),
                        userInfo.getBirthDate()
                );
                user = userJpaRepository.save(user);
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

        UserPrincipal userPrincipal = UserPrincipal.from(UserMapper.toDomain(user));
        String accessToken = jwtService.generateAccessToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);
        refreshTokenRepository.save(user.getEmail(), refreshToken, jwtService.getRefreshTokenExpiration());

        return userPrincipal;
    }
}
