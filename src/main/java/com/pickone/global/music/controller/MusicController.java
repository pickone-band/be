package com.pickone.global.music.controller;

import com.pickone.global.music.dto.MusicSyncResult;
import com.pickone.global.music.dto.PlaybackInfoResponse;
import com.pickone.global.music.dto.PlaylistInfoResponse;
import com.pickone.global.music.dto.SocialMusicTrackResponse;
import com.pickone.global.music.service.MusicSyncService;
import com.pickone.global.music.service.SocialMusicApiClient;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.oauth2.entity.UserConnection;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.repository.UserConnectionRepository;
import com.pickone.global.music.dto.MusicInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Music", description = "소셜 뮤직 연동 및 재생 정보 관련 API")
public class MusicController {

  private final UserConnectionRepository connectionRepository;
  private final Map<OAuth2Provider, SocialMusicApiClient> musicApiClients;
  private final MusicSyncService musicSyncService;

  private UserConnection getUserConnection(Long userId, String providerStr) {
    OAuth2Provider provider = OAuth2Provider.from(providerStr);

    log.info("[MusicController] 사용자 연결 조회 시도 - 사용자 ID: {}, 제공자: {}", userId, provider);
    return connectionRepository.findByProviderAndUserId(provider, userId)
        .orElseThrow(() -> {
          log.warn("[MusicController] 사용자 소셜 연결 정보 없음 - 사용자 ID: {}, 제공자: {}", userId, provider);
          return new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND);
        });
  }

  @Operation(summary = "현재 재생 중인 트랙 조회", description = "현재 사용자가 해당 플랫폼(Spotify 등)에서 재생 중인 곡 정보를 반환합니다.")
  @GetMapping("/current/{provider}")
  public ResponseEntity<?> getCurrentTrack(
      @AuthenticationPrincipal(expression = "id") Long userId,
      @PathVariable String provider) {

    log.info("[MusicController] 현재 재생 중인 트랙 조회 요청 - provider: {}", provider);
    UserConnection connection = getUserConnection(userId, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());

    SocialMusicTrackResponse track = client.getCurrentlyPlaying(connection.getAccessToken());
    MusicInfoResponse info = track != null ? track.toMusicInfoResponse() : null;

    log.debug("[MusicController] 현재 트랙 조회 결과 - 사용자 ID: {}, 트랙 존재 여부: {}", connection.getUserId(), track != null);
    return BaseResponse.success(SuccessCode.OK, info);
  }

  @Operation(summary = "현재 재생 상태 전체 정보 조회", description = "현재 곡, 디바이스, 활성 상태, 플레이리스트 등의 정보를 반환합니다.")
  @GetMapping("/playback/{provider}")
  public ResponseEntity<?> getPlaybackInfo(
      @AuthenticationPrincipal(expression = "id") Long userId,
      @PathVariable String provider) {

    log.info("[MusicController] 현재 재생 상태 조회 요청 - provider: {}", provider);
    UserConnection connection = getUserConnection(userId, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());

    SocialMusicTrackResponse track = client.getCurrentlyPlaying(connection.getAccessToken());
    MusicInfoResponse info = track != null ? track.toMusicInfoResponse() : null;

    PlaybackInfoResponse response = new PlaybackInfoResponse(
        info,
        client.getPlaylists(connection.getAccessToken()),
        client.getDeviceName(connection.getAccessToken()),
        client.isActiveDevice(connection.getAccessToken())
    );

    log.debug("[MusicController] 재생 상태 응답 - 사용자 ID: {}, 트랙: {}, 디바이스: {}",
        connection.getUserId(), info != null ? info.title() : "없음", response.deviceName());

    return BaseResponse.success(SuccessCode.OK, response);
  }

  @Operation(summary = "사용자 플레이리스트 조회", description = "연결된 플랫폼 계정에서 사용자의 플레이리스트 목록을 조회합니다.")
  @GetMapping("/playlists/{provider}")
  public ResponseEntity<?> getPlaylists(
      @AuthenticationPrincipal(expression = "id") Long userId,
      @PathVariable String provider) {

    log.info("[MusicController] 사용자 플레이리스트 조회 요청 - provider: {}", provider);
    UserConnection connection = getUserConnection(userId, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());

    List<PlaylistInfoResponse> playlists = client.getPlaylists(connection.getAccessToken());
    log.debug("[MusicController] 플레이리스트 개수: {} - 사용자 ID: {}", playlists.size(), connection.getUserId());
    return BaseResponse.success(SuccessCode.OK, playlists);
  }

  @Operation(summary = "음악 동기화", description = "플랫폼에서 사용자의 음악 데이터를 불러와 DB에 동기화합니다.")
  @PostMapping("/sync/{provider}")
  public ResponseEntity<?> syncMusic(
      @AuthenticationPrincipal(expression = "id") Long userId,
      @PathVariable String provider) {

    log.info("[MusicController] 음악 동기화 요청 - provider: {}", provider);
    UserConnection connection = getUserConnection(userId, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());

    List<SocialMusicTrackResponse> tracks = client.getTracks(connection.getAccessToken());
    log.info("[MusicController] 트랙 수신 완료 - 수신 트랙 수: {}, 사용자 ID: {}", tracks.size(), connection.getUserId());

    List<MusicInfoResponse> converted = tracks.stream()
        .map(SocialMusicTrackResponse::toMusicInfoResponse)
        .toList();

    MusicSyncResult result = musicSyncService.syncUserMusic(connection.getUserId(), converted);
    log.info("[MusicController] 음악 동기화 완료 - 신규: {}, 기존: {}", result.newCount(), result.existCount());

    return BaseResponse.success(SuccessCode.OK, result);
  }
}
