package com.PickOne.global.oauth2.service;

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

        Optional<UserConnectionEntity> connectionOpt = userConnectionRepository.findByProviderAndProviderUserId(
                provider.name(), userInfo.getId()
        );

        UserEntity user;
        if (connectionOpt.isPresent()) {
            user = userJpaRepository.findById(connectionOpt.get().getUserId())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            user = userJpaRepository.findByEmail(userInfo.getEmail())
                    .orElseGet(() -> {
                        UserEntity newUser = UserEntity.builder()
                                .email(userInfo.getEmail())
                                .password(passwordEncoder.encode("oauth2TempPass" + userInfo.getEmail()))
                                .nickname(userInfo.getNickname())
                                .profileImage(userInfo.getProfileImageUrl())
                                .role(Role.USER)
                                .isPublic(true)
                                .isOauth(true)
                                .gender(userInfo.getGender())
                                .birthDate(userInfo.getBirthDate())
                                .genres(List.of())
                                .mbti(null)
                                .build();
                        return userJpaRepository.save(newUser);
                    });

            UserConnectionEntity connection = UserConnectionEntity.builder()
                    .provider(provider)
                    .providerUserId(userInfo.getId())
                    .email(userInfo.getEmail())
                    .nickname(userInfo.getNickname())
                    .userId(user.getId())
                    .build();
            userConnectionRepository.save(connection);
        }

        UserPrincipal principal = UserPrincipal.from(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);
        refreshTokenRepository.save(user.getEmail(), refreshToken, jwtService.getRefreshTokenExpiration());

        return principal;
    }
}

