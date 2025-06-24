package com.pickone.global.music.controller;

import com.pickone.global.music.dto.MusicInfo;
import com.pickone.global.music.dto.PlaybackInfoResponse;
import com.pickone.global.music.dto.PlaylistInfo;
import com.pickone.global.music.service.GoogleMusicService;
import com.pickone.global.music.service.SpotifyMusicService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.model.entity.UserConnectionEntity;
import com.pickone.global.oauth2.repository.UserConnectionRepository;
import com.pickone.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

  private final JwtService jwtService;
  private final UserConnectionRepository connectionRepository;
  private final SpotifyMusicService spotifyMusicService;
  private final GoogleMusicService googleMusicService;

  @GetMapping("/current/{provider}")
  public ResponseEntity<?> getCurrentTrack(@RequestHeader("Authorization") String authorization,
      @PathVariable String provider) {
    log.info("현재 트랙 조회 요청: provider={}", provider);

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      log.warn("Authorization 헤더 누락 또는 형식 오류");
      throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
    }

    String token = authorization.substring(7);
    Long userId;
    try {
      userId = jwtService.getUserIdFromToken(token);
      log.info("JWT 토큰 파싱 성공: userId={}", userId);
    } catch (Exception e) {
      log.warn("JWT 토큰 파싱 실패");
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    OAuth2Provider oAuth2Provider;
    try {
      oAuth2Provider = OAuth2Provider.from(provider);
    } catch (IllegalArgumentException e) {
      log.warn("지원하지 않는 소셜 provider: {}", provider);
      throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }

    Optional<UserConnectionEntity> connectionOpt =
        connectionRepository.findByProviderAndUserId(oAuth2Provider, userId);
    if (connectionOpt.isEmpty()) {
      log.warn("해당 provider에 연결된 소셜 계정 없음: provider={}, userId={}", oAuth2Provider, userId);
      throw new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND);
    }

    String providerAccessToken = connectionOpt.get().getAccessToken();
    MusicInfo info = null;

    switch (oAuth2Provider) {
      case SPOTIFY:
        info = spotifyMusicService.getCurrentlyPlaying(providerAccessToken);
        break;
      case GOOGLE:
        info = googleMusicService.getCurrentlyPlaying(providerAccessToken);
        break;
      default:
        log.warn("지원되지 않는 provider: {}", oAuth2Provider);
        break;
    }

    if (info == null) {
      log.info("현재 재생 중인 트랙 없음");
      return BaseResponse.success(); // 200 OK, body 없이 성공
    }

    log.info("현재 트랙 응답 반환: {}", info.title());
    return BaseResponse.success(SuccessCode.OK, info);
  }

  @GetMapping("/playback/{provider}")
  public ResponseEntity<?> getPlaybackInfo(@RequestHeader("Authorization") String authorization,
      @PathVariable String provider) {
    log.info("재생 정보 조회 요청: provider={}", provider);

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      log.warn("Authorization 헤더 누락 또는 형식 오류");
      throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
    }

    String token = authorization.substring(7);
    Long userId;
    try {
      userId = jwtService.getUserIdFromToken(token);
      log.info("JWT 토큰 파싱 성공: userId={}", userId);
    } catch (Exception e) {
      log.warn("JWT 토큰 파싱 실패");
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    OAuth2Provider oAuth2Provider;
    try {
      oAuth2Provider = OAuth2Provider.from(provider);
    } catch (IllegalArgumentException e) {
      log.warn("지원하지 않는 소셜 provider: {}", provider);
      throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }

    UserConnectionEntity connection = connectionRepository
        .findByProviderAndUserId(oAuth2Provider, userId)
        .orElseThrow(() -> {
          log.warn("해당 provider에 연결된 소셜 계정 없음: provider={}, userId={}", oAuth2Provider, userId);
          return new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND);
        });

    String accessToken = connection.getAccessToken();

    MusicInfo current = null;
    List<PlaylistInfo> playlists = null;
    String deviceName = null;
    boolean active = false;

    switch (oAuth2Provider) {
      case SPOTIFY:
        current = spotifyMusicService.getCurrentlyPlaying(accessToken);
        playlists = spotifyMusicService.getPlaylists(accessToken);
        deviceName = spotifyMusicService.getDeviceName(accessToken);
        active = spotifyMusicService.isActiveDevice(accessToken);
        break;
      case GOOGLE:
        current = googleMusicService.getCurrentlyPlaying(accessToken);
        playlists = googleMusicService.getPlaylists(accessToken);
        deviceName = googleMusicService.getDeviceName(accessToken);
        active = googleMusicService.isActiveDevice(accessToken);
        break;
      default:
        log.warn("지원되지 않는 provider: {}", oAuth2Provider);
        throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
    }

    PlaybackInfoResponse response = new PlaybackInfoResponse(
        current,
        playlists,
        deviceName,
        active
    );

    log.info("재생 정보 응답 반환: userId={}, provider={}, playlistCount={}, active={}",
        userId, provider, playlists != null ? playlists.size() : 0, active);

    return BaseResponse.success(SuccessCode.OK, response);
  }
}
