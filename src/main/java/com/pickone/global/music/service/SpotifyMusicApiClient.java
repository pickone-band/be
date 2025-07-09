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
@Service("spotifyMusicApiClient")
@RequiredArgsConstructor
public class SpotifyMusicApiClient implements SocialMusicApiClient {
  private final WebClient webClient;

  @Override
  public List<PlaylistInfoResponse> getPlaylists(String accessToken) {
    var response = webClient.get()
        .uri("/me/playlists")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    List<PlaylistInfoResponse> result = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode item : response.get("items")) {
        result.add(new PlaylistInfoResponse(
            item.get("id").asText(),
            item.get("name").asText(),
            item.has("description") ? item.get("description").asText() : "",
            item.has("images") && !item.get("images").isEmpty() ? item.get("images").get(0).get("url").asText() : null,
            item.path("tracks").path("total").asInt(0)
        ));
      }
    }
    return result;
  }

  @Override
  public SocialMusicTrackResponse getCurrentlyPlaying(String accessToken) {
    var response = webClient.get()
        .uri("/me/player/currently-playing")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    if (response == null || !response.has("item") || response.get("item").isNull()) return null;
    JsonNode item = response.get("item");
    return new SocialMusicTrackResponse(
        item.get("name").asText(),
        item.get("artists").get(0).get("name").asText(),
        item.get("album").get("name").asText(),
        null,
        item.get("id").asText(),
        item.path("album").path("images").isEmpty() ? null : item.get("album").get("images").get(0).get("url").asText(),
        item.path("external_urls").path("spotify").asText()
    );
  }

  @Override
  public List<SocialMusicTrackResponse> getTracks(String accessToken) {
    var response = webClient.get()
        .uri("/me/player/recently-played?limit=20")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    List<SocialMusicTrackResponse> tracks = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode play : response.get("items")) {
        JsonNode track = play.get("track");
        tracks.add(new SocialMusicTrackResponse(
            track.get("name").asText(),
            track.get("artists").get(0).get("name").asText(),
            track.get("album").get("name").asText(),
            null,
            track.get("id").asText(),
            track.path("album").path("images").isEmpty() ? null : track.get("album").get("images").get(0).get("url").asText(),
            track.path("external_urls").path("spotify").asText()
        ));
      }
    }
    return tracks;
  }

  @Override
  public String getDeviceName(String accessToken) {
    var response = webClient.get()
        .uri("/me/player/devices")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    if (response == null || !response.has("devices")) return null;
    for (JsonNode device : response.get("devices")) {
      if (device.path("is_active").asBoolean(false)) {
        return device.path("name").asText();
      }
    }
    return null;
  }

  @Override
  public boolean isActiveDevice(String accessToken) {
    var response = webClient.get()
        .uri("/me/player/devices")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    if (response == null || !response.has("devices")) return false;
    for (JsonNode device : response.get("devices")) {
      if (device.path("is_active").asBoolean(false)) return true;
    }
    return false;
  }
}
