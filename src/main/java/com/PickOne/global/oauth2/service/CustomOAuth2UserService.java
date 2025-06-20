package com.PickOne.global.oauth2.service;

import com.PickOne.domain.music.dto.MusicInfo;
import com.PickOne.domain.music.repository.UserMusicJpaRepository;
import com.PickOne.domain.music.service.GoogleMusicService;
import com.PickOne.domain.music.service.SpotifyMusicService;
import com.PickOne.domain.user.model.domain.Role;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.model.entity.UserMusicEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.domain.OAuth2UserInfo;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import com.PickOne.global.oauth2.repository.UserConnectionRepository;
import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.repository.RefreshTokenRepository;
import com.PickOne.global.security.service.TokenProvider;
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
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SpotifyMusicService spotifyMusicService;
    private final GoogleMusicService googleMusicService;
    private final UserMusicJpaRepository userMusicJpaRepository;

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

        String providerAccessToken = userRequest.getAccessToken().getTokenValue();
        String providerRefreshToken = (String) userRequest.getAdditionalParameters().get("refresh_token");

        UserEntity user;
        if (connectionOpt.isPresent()) {
            UserConnectionEntity connection = connectionOpt.get();
            connection.updateTokens(providerAccessToken, providerRefreshToken);
            userConnectionRepository.save(connection);
            user = userJpaRepository.findById(connection.getUserId())
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
                    .accessToken(providerAccessToken)
                    .refreshToken(providerRefreshToken)
                    .userId(user.getId())
                    .build();
            userConnectionRepository.save(connection);
        }

        UserPrincipal principal = UserPrincipal.from(user);
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = tokenProvider.generateRefreshToken(principal);
        refreshTokenRepository.save(user.getEmail(), refreshToken, tokenProvider.getRefreshTokenExpiration());

        saveCurrentMusicForUser(user, providerAccessToken, provider);

        return principal;
    }

    public void saveCurrentMusicForUser(UserEntity user, String accessToken, OAuth2Provider provider) {
        MusicInfo track = switch (provider) {
            case SPOTIFY -> spotifyMusicService.getCurrentlyPlaying(accessToken);
            case GOOGLE -> googleMusicService.getCurrentlyPlaying(accessToken);
            default -> null;
        };

        if (track != null) {
            UserMusicEntity music = UserMusicEntity.builder()
                    .title(track.title())
                    .artist(track.artist())
                    .album(track.album())
                    .imageUrl(track.imageUrl())
                    .trackUrl(track.trackUrl())
                    .user(user)
                    .build();
            userMusicJpaRepository.save(music);
        }
    }
}

