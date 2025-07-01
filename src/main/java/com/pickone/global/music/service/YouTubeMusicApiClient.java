package com.pickone.global.music.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pickone.global.music.dto.PlaylistInfoDto;
import com.pickone.global.music.dto.SocialMusicTrackDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service("youtubeMusicApiClient")
@RequiredArgsConstructor
@Slf4j
public class YouTubeMusicApiClient implements SocialMusicApiClient {

  private final WebClient webClient;

  @Override
  public List<PlaylistInfoDto> getPlaylists(String accessToken) {
    var response = webClient.get()
        .uri(uriBuilder -> uriBuilder.path("/playlists")
            .queryParam("part", "snippet")
            .queryParam("mine", true)
            .queryParam("maxResults", 10)
            .build())
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    List<PlaylistInfoDto> result = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode item : response.get("items")) {
        JsonNode snippet = item.get("snippet");
        result.add(new PlaylistInfoDto(
            item.get("id").asText(),
            snippet.get("title").asText(),
            snippet.has("description") ? snippet.get("description").asText() : "",
            snippet.has("thumbnails") && snippet.get("thumbnails").has("default")
                ? snippet.get("thumbnails").get("default").get("url").asText() : null,
            0 // YouTube는 트랙 수 미지원
        ));
      }
    }
    return result;
  }

  @Override
  public SocialMusicTrackDto getCurrentlyPlaying(String accessToken) {
    // 유튜브 Music은 현재 재생 트랙 API 미지원 (null 반환)
    return null;
  }

  @Override
  public List<SocialMusicTrackDto> getTracks(String accessToken) {
    // 필요하면 별도 구현
    return List.of();
  }

  @Override
  public String getDeviceName(String accessToken) {
    return "YouTube Web";
  }

  @Override
  public boolean isActiveDevice(String accessToken) {
    return false;
  }
}
