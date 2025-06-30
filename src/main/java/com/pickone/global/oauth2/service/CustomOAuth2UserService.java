package com.pickone.global.oauth2.service;

import com.pickone.domain.user.model.vo.UserAuthInfo;
import com.pickone.domain.user.model.vo.UserPreference;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.model.vo.UserStatus;
import com.pickone.global.music.dto.MusicInfo;
import com.pickone.global.music.repository.UserMusicJpaRepository;
import com.pickone.global.music.service.GoogleMusicService;
import com.pickone.global.music.service.SpotifyMusicService;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserMusicEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.oauth2.model.entity.UserConnectionEntity;
import com.pickone.global.oauth2.repository.UserConnectionRepository;
import com.pickone.global.security.model.entity.UserPrincipal;
import com.pickone.global.security.repository.RefreshTokenRepository;
import com.pickone.global.security.service.TokenProvider;
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
        log.info("OAuth2 로그인 요청: provider={}", userRequest.getClientRegistration().getRegistrationId());

        OAuth2User oAuth2User = loadOAuth2User(userRequest);

        String providerName = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Provider provider = OAuth2Provider.from(providerName);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(provider, attributes);

        log.debug("OAuth2 사용자 정보 수신: email={}, nickname={}", userInfo.getEmail(), userInfo.getNickname());

        Optional<UserConnectionEntity> connectionOpt = userConnectionRepository.findByProviderAndProviderUserId(
            provider.name(), userInfo.getId()
        );

        String providerAccessToken = userRequest.getAccessToken().getTokenValue();
        String providerRefreshToken = (String) userRequest.getAdditionalParameters().get("refresh_token");

        UserEntity user;

        if (connectionOpt.isPresent()) {
            log.info("기존 소셜 연결 계정 발견: providerUserId={}", userInfo.getId());
            UserConnectionEntity connection = connectionOpt.get();
            connection.updateTokens(providerAccessToken, providerRefreshToken);
            userConnectionRepository.save(connection);

            user = userJpaRepository.findById(connection.getUserId())
                .orElseThrow(() -> {
                    log.error("연결된 사용자 ID에 해당하는 사용자 없음: id={}", connection.getUserId());
                    return new IllegalStateException("User not found");
                });

        } else {
            log.info("신규 사용자 또는 소셜 연결 없음, 사용자 생성 여부 확인: email={}", userInfo.getEmail());

            user = userJpaRepository.findByProfile_Email(userInfo.getEmail())
                .orElseGet(() -> {
                    log.info("신규 사용자 생성: email={}", userInfo.getEmail());
                    UserEntity newUser = UserEntity.builder()
                        .profile(UserProfile.of(
                            userInfo.getEmail(),
                            passwordEncoder.encode("oauth2TempPass" + userInfo.getEmail()),
                            userInfo.getNickname(),
                            null,
                            userInfo.getGender(),
                            null,
                            null))
                        .status(UserStatus.init())
                        .preference(UserPreference.ofNullable(List.of()))
                        .authInfo(UserAuthInfo.of(true))
                        .role(Role.USER)
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
            log.info("소셜 연결 정보 저장 완료: userId={}, provider={}", user.getId(), provider);
        }

        UserPrincipal principal = UserPrincipal.from(user);
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = tokenProvider.generateRefreshToken(principal);

        refreshTokenRepository.save(user.getProfile().getEmail(), refreshToken, tokenProvider.getRefreshTokenExpiration());
        log.info("JWT 토큰 발급 완료: accessToken=..., refreshToken 저장 완료");

        saveCurrentMusicForUser(user, providerAccessToken, provider);

        return principal;
    }

    public void saveCurrentMusicForUser(UserEntity user, String accessToken, OAuth2Provider provider) {
        log.info("OAuth 로그인 시 현재 음악 저장 시도: userId={}, provider={}", user.getId(), provider);

        MusicInfo track = null;
        switch (provider) {
            case SPOTIFY:
                track = spotifyMusicService.getCurrentlyPlaying(accessToken);
                break;
            case GOOGLE:
                track = googleMusicService.getCurrentlyPlaying(accessToken);
                break;
            default:
                log.warn("지원되지 않는 provider에 대한 음악 저장 요청: {}", provider);
        }

        if (track != null) {
            log.info("현재 재생 중인 음악 저장: title={}, artist={}, album={}", track.title(), track.artist(), track.album());

            UserMusicEntity music = UserMusicEntity.builder()
                .title(track.title())
                .artist(track.artist())
                .album(track.album())
                .imageUrl(track.imageUrl())
                .trackUrl(track.trackUrl())
                .user(user)
                .build();

            userMusicJpaRepository.save(music);
        } else {
            log.info("재생 중인 음악 정보 없음 또는 저장 생략: userId={}", user.getId());
        }
    }
}
