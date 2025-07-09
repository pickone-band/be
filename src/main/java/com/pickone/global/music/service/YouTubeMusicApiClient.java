package com.pickone.global.music.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pickone.global.music.dto.PlaylistInfoResponse;
import com.pickone.global.music.dto.SocialMusicTrackResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("youtubeMusicApiClient")
@RequiredArgsConstructor
public class YouTubeMusicApiClient implements SocialMusicApiClient {
  private final WebClient webClient;

  @Override
  public List<PlaylistInfoResponse> getPlaylists(String accessToken) {
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

    List<PlaylistInfoResponse> result = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode item : response.get("items")) {
        JsonNode snippet = item.get("snippet");
        result.add(new PlaylistInfoResponse(
            item.get("id").asText(),
            snippet.get("title").asText(),
            snippet.has("description") ? snippet.get("description").asText() : "",
            snippet.path("thumbnails").has("default") ? snippet.get("thumbnails").get("default").get("url").asText() : null,
            0
        ));
      }
    }
    return result;
  }

  @Override
  public SocialMusicTrackResponse getCurrentlyPlaying(String accessToken) {
    return null; // 미지원
  }

  @Override
  public List<SocialMusicTrackResponse> getTracks(String accessToken) {
    return List.of(); // 미지원
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
