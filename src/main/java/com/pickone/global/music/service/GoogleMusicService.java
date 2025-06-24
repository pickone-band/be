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
public class GoogleMusicService {

  private final WebClient webClient = WebClient.builder()
      .baseUrl("https://www.googleapis.com/youtube/v3")
      .build();

  public MusicInfo getCurrentlyPlaying(String accessToken) {
    log.info("YouTube 현재 재생 트랙 조회 요청 (불가)");
    return null;
  }

  public List<PlaylistInfo> getPlaylists(String accessToken) {
    log.info("YouTube 재생목록 조회 요청");

    var response = webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/playlists")
            .queryParam("part", "snippet")
            .queryParam("mine", true)
            .queryParam("maxResults", 10)
            .build())
        .headers(headers -> headers.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    List<PlaylistInfo> result = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode item : response.get("items")) {
        JsonNode snippet = item.get("snippet");
        result.add(new PlaylistInfo(
            item.get("id").asText(),
            snippet.get("title").asText(),
            snippet.has("description") ? snippet.get("description").asText() : "",
            snippet.has("thumbnails") && snippet.get("thumbnails").has("default")
                ? snippet.get("thumbnails").get("default").get("url").asText() : null,
            0
        ));
      }
    }

    log.info("YouTube 재생목록 개수: {}", result.size());
    return result;
  }

  public String getDeviceName(String accessToken) {
    log.info("YouTube 디바이스 이름 요청 (고정값)");
    return "YouTube Web";
  }

  public boolean isActiveDevice(String accessToken) {
    log.info("YouTube 활성 디바이스 존재 여부 요청 (항상 false)");
    return false;
  }
}
