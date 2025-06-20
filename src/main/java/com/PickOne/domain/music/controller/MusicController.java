package com.PickOne.domain.music.controller;

import com.PickOne.domain.music.dto.MusicInfo;
import com.PickOne.domain.music.dto.PlaybackInfoResponse;
import com.PickOne.domain.music.dto.PlaylistInfo;
import com.PickOne.domain.music.service.GoogleMusicService;
import com.PickOne.domain.music.service.SpotifyMusicService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.exception.SuccessCode;

import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
import com.PickOne.global.oauth2.repository.UserConnectionRepository;
import com.PickOne.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
        }

        String token = authorization.substring(7);
        Long userId;
        try {
            userId = jwtService.getUserIdFromToken(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        OAuth2Provider oAuth2Provider;
        try {
            oAuth2Provider = OAuth2Provider.from(provider);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
        }

        Optional<UserConnectionEntity> connectionOpt =
                connectionRepository.findByProviderAndUserId(oAuth2Provider, userId);
        if (connectionOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND);
        }

        String providerAccessToken = connectionOpt.get().getAccessToken();
        MusicInfo info = switch (oAuth2Provider) {
            case SPOTIFY -> spotifyMusicService.getCurrentlyPlaying(providerAccessToken);
            case GOOGLE -> googleMusicService.getCurrentlyPlaying(providerAccessToken);
            default -> null;
        };

        if (info == null) {
            return BaseResponse.success(); // 200 OK, body 없이 성공
        }

        return BaseResponse.success(SuccessCode.OK, info);
    }

    @GetMapping("/playback/{provider}")
    public ResponseEntity<?> getPlaybackInfo(@RequestHeader("Authorization") String authorization,
                                             @PathVariable String provider) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
        }

        String token = authorization.substring(7);
        Long userId = jwtService.getUserIdFromToken(token);

        OAuth2Provider oAuth2Provider = OAuth2Provider.from(provider);
        UserConnectionEntity connection = connectionRepository
                .findByProviderAndUserId(oAuth2Provider, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SOCIAL_ACCOUNT_NOT_FOUND));

        String accessToken = connection.getAccessToken();

        MusicInfo current;
        List<PlaylistInfo> playlists;
        String deviceName;
        boolean active;

        switch (oAuth2Provider) {
            case SPOTIFY -> {
                current = spotifyMusicService.getCurrentlyPlaying(accessToken);
                playlists = spotifyMusicService.getPlaylists(accessToken);
                deviceName = spotifyMusicService.getDeviceName(accessToken);
                active = spotifyMusicService.isActiveDevice(accessToken);
            }
            case GOOGLE -> {
                current = googleMusicService.getCurrentlyPlaying(accessToken);
                playlists = googleMusicService.getPlaylists(accessToken);
                deviceName = googleMusicService.getDeviceName(accessToken);
                active = googleMusicService.isActiveDevice(accessToken);
            }
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
        }

        PlaybackInfoResponse response = new PlaybackInfoResponse(
                current,
                playlists,
                deviceName,
                active
        );

        return BaseResponse.success(SuccessCode.OK, response);
    }

}