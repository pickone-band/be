package com.pickone.global.music.service;

import com.pickone.global.music.dto.MusicInfo;
import com.pickone.global.music.dto.PlaylistInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyMusicService {

  private final WebClient webClient = WebClient.builder()
      .baseUrl("https://api.spotify.com/v1")
      .build();

  public MusicInfo getCurrentlyPlaying(String accessToken) {
    log.info("Spotify 현재 재생 트랙 조회 요청");

    var response = webClient.get()
        .uri("/me/player/currently-playing")
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    if (response == null || !response.has("item")) {
      log.warn("Spotify 현재 재생 항목 없음");
      return null;
    }

    JsonNode item = response.get("item");
    log.info("현재 재생 트랙 이름: {}", item.get("name").asText());
    return new MusicInfo(
        item.get("name").asText(),
        item.get("artists").get(0).get("name").asText(),
        item.get("album").get("name").asText(),
        item.get("album").get("images").get(0).get("url").asText(),
        item.get("external_urls").get("spotify").asText()
    );
  }

  public List<PlaylistInfo> getPlaylists(String accessToken) {
    log.info("Spotify 재생목록 조회 요청");

    var response = webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/me/playlists").queryParam("limit", 10).build())
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    List<PlaylistInfo> result = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode item : response.get("items")) {
        result.add(new PlaylistInfo(
            item.get("id").asText(),
            item.get("name").asText(),
            item.has("description") ? item.get("description").asText() : "",
            item.get("images").isEmpty() ? null : item.get("images").get(0).get("url").asText(),
            item.get("tracks").get("total").asInt()
        ));
      }
    }

    log.info("Spotify 재생목록 개수: {}", result.size());
    return result;
  }

  public String getDeviceName(String accessToken) {
    log.info("Spotify 디바이스 이름 조회 요청");

    var response = webClient.get()
        .uri("/me/player/devices")
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    if (response == null || !response.has("devices")) {
      log.warn("Spotify 디바이스 정보 없음");
      return null;
    }

    for (JsonNode device : response.get("devices")) {
      if (device.get("is_active").asBoolean()) {
        log.info("활성 디바이스 이름: {}", device.get("name").asText());
        return device.get("name").asText();
      }
    }

    log.warn("활성화된 Spotify 디바이스 없음");
    return null;
  }

  public boolean isActiveDevice(String accessToken) {
    log.info("Spotify 활성 디바이스 존재 여부 확인 요청");

    var response = webClient.get()
        .uri("/me/player/devices")
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    if (response == null || !response.has("devices")) {
      return false;
    }

    for (JsonNode device : response.get("devices")) {
      if (device.get("is_active").asBoolean()) {
        log.info("Spotify 활성 디바이스 존재");
        return true;
      }
    }

    log.info("Spotify 활성 디바이스 없음");
    return false;
  }
}
