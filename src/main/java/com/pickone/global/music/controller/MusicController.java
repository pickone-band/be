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

  // 현재 트랙
  @GetMapping("/current/{provider}")
  public ResponseEntity<?> getCurrentTrack(@RequestHeader("Authorization") String authorization,
      @PathVariable String provider) {
    UserConnectionEntity connection = getUserConnection(authorization, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());
    SocialMusicTrackDto track = client.getCurrentlyPlaying(connection.getAccessToken());
    MusicInfoDto info = track != null ? track.toMusicInfoDto() : null;
    return BaseResponse.success(SuccessCode.OK, info);
  }

  // 재생 정보(플레이리스트/디바이스/활성 상태)
  @GetMapping("/playback/{provider}")
  public ResponseEntity<?> getPlaybackInfo(@RequestHeader("Authorization") String authorization,
      @PathVariable String provider) {
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

  // 플레이리스트
  @GetMapping("/playlists/{provider}")
  public ResponseEntity<?> getPlaylists(@RequestHeader("Authorization") String authorization,
      @PathVariable String provider) {
    UserConnectionEntity connection = getUserConnection(authorization, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());
    List<PlaylistInfoDto> playlists = client.getPlaylists(connection.getAccessToken());
    return BaseResponse.success(SuccessCode.OK, playlists);
  }

  // [동기화] 플랫폼 음악 → DB 반영
  @PostMapping("/sync/{provider}")
  public ResponseEntity<?> syncMusic(@RequestHeader("Authorization") String authorization,
      @PathVariable String provider) {
    UserConnectionEntity connection = getUserConnection(authorization, provider);
    SocialMusicApiClient client = musicApiClients.get(connection.getProvider());
    List<SocialMusicTrackDto> tracks = client.getTracks(connection.getAccessToken());
    var result = musicSyncService.syncUserMusic(connection.getUserId(), tracks);
    return BaseResponse.success(SuccessCode.OK, result);
  }
}
