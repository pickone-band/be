package com.pickone.global.music.controller;

import com.pickone.global.music.dto.MusicInfoDto;
import com.pickone.global.music.dto.PlaybackInfoResponse;
import com.pickone.global.music.dto.PlaylistInfoDto;
import com.pickone.global.music.dto.SocialMusicTrackDto;
import com.pickone.global.music.service.MusicSyncService;
import com.pickone.global.music.service.SocialMusicApiClient;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.oauth2.model.domain.OAuth2Provider;
import com.pickone.global.oauth2.model.entity.UserConnectionEntity;
import com.pickone.global.oauth2.repository.UserConnectionRepository;
import com.pickone.global.security.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Music", description = "소셜 뮤직 연동 및 재생 정보 관련 API")
public class MusicController {

  private final JwtService jwtService;
  private final UserConnectionRepository connectionRepository;
  private final Map<OAuth2Provider, SocialMusicApiClient> musicApiClients;
  private final MusicSyncService musicSyncService;

  private UserConnectionEntity getUserConnection(String authorization, String providerStr) {
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
    }
    String token = authorization.substring(7);
    Long userId = jwtService.getUserIdFromToken(token);

    OAuth2Provider provider = OAuth2Provider.from(providerStr);
    return connectionRepository.findByProviderAndUserId(provider, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));
  }

  @Operation(summary = "현재 재생 중인 트랙 조회", description = "현재 사용자가 해당 플랫폼(Spotify 등)에서 재생 중인 곡 정보를 반환합니다.")
  @GetMapping("/current/{provider}")
  public ResponseEntity<?> getCurrentTrack(
      @Parameter(description = "Authorization 헤더 (Bearer 토큰)") @RequestHeader("Authorization") String authorization,
      @Parameter(description = "뮤직 플랫폼 이름 (예: spotify)") @PathVariable String provider) {
    UserConnectionEntity connection = getUserConnection(authorization, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());
    SocialMusicTrackDto track = client.getCurrentlyPlaying(connection.getAccessToken());
    MusicInfoDto info = track != null ? track.toMusicInfoDto() : null;
    return BaseResponse.success(SuccessCode.OK, info);
  }

  @Operation(summary = "현재 재생 상태 전체 정보 조회", description = "현재 곡, 디바이스, 활성 상태, 플레이리스트 등의 정보를 반환합니다.")
  @GetMapping("/playback/{provider}")
  public ResponseEntity<?> getPlaybackInfo(
      @Parameter(description = "Authorization 헤더 (Bearer 토큰)") @RequestHeader("Authorization") String authorization,
      @Parameter(description = "뮤직 플랫폼 이름 (예: spotify)") @PathVariable String provider) {
    UserConnectionEntity connection = getUserConnection(authorization, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());
    SocialMusicTrackDto track = client.getCurrentlyPlaying(connection.getAccessToken());
    MusicInfoDto info = track != null ? track.toMusicInfoDto() : null;

    PlaybackInfoResponse response = new PlaybackInfoResponse(
        info,
        client.getPlaylists(connection.getAccessToken()),
        client.getDeviceName(connection.getAccessToken()),
        client.isActiveDevice(connection.getAccessToken())
    );
    return BaseResponse.success(SuccessCode.OK, response);
  }

  @Operation(summary = "사용자 플레이리스트 조회", description = "연결된 플랫폼 계정에서 사용자의 플레이리스트 목록을 조회합니다.")
  @GetMapping("/playlists/{provider}")
  public ResponseEntity<?> getPlaylists(
      @Parameter(description = "Authorization 헤더 (Bearer 토큰)") @RequestHeader("Authorization") String authorization,
      @Parameter(description = "뮤직 플랫폼 이름 (예: spotify)") @PathVariable String provider) {
    UserConnectionEntity connection = getUserConnection(authorization, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());
    List<PlaylistInfoDto> playlists = client.getPlaylists(connection.getAccessToken());
    return BaseResponse.success(SuccessCode.OK, playlists);
  }

  @Operation(summary = "음악 동기화", description = "플랫폼에서 사용자의 음악 데이터를 불러와 DB에 동기화합니다.")
  @PostMapping("/sync/{provider}")
  public ResponseEntity<?> syncMusic(
      @Parameter(description = "Authorization 헤더 (Bearer 토큰)") @RequestHeader("Authorization") String authorization,
      @Parameter(description = "뮤직 플랫폼 이름 (예: spotify)") @PathVariable String provider) {
    UserConnectionEntity connection = getUserConnection(authorization, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());
    List<SocialMusicTrackDto> tracks = client.getTracks(connection.getAccessToken());
    var result = musicSyncService.syncUserMusic(connection.getUserId(), tracks);
    return BaseResponse.success(SuccessCode.OK, result);
  }
}
