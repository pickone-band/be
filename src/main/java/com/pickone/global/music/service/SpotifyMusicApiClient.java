package com.pickone.global.music.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.pickone.global.music.dto.PlaylistInfoDto;
import com.pickone.global.music.dto.SpotifyTrackDto;
import com.pickone.global.music.dto.SocialMusicTrackDto;
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
  public List<PlaylistInfoDto> getPlaylists(String accessToken) {
    var response = webClient.get()
        .uri("/me/playlists")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    List<PlaylistInfoDto> result = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode item : response.get("items")) {
        result.add(new PlaylistInfoDto(
            item.get("id").asText(),
            item.get("name").asText(),
            item.has("description") ? item.get("description").asText() : "",
            item.has("images") && !item.get("images").isEmpty() ? item.get("images").get(0).get("url").asText() : null,
            item.has("tracks") && item.get("tracks").has("total") ? item.get("tracks").get("total").asInt() : 0
        ));
      }
    }
    return result;
  }

  @Override
  public SocialMusicTrackDto getCurrentlyPlaying(String accessToken) {
    var response = webClient.get()
        .uri("/me/player/currently-playing")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    if (response == null || !response.has("item") || response.get("item").isNull()) return null;
    JsonNode item = response.get("item");
    SpotifyTrackDto spotifyDto = new SpotifyTrackDto(
        item.get("id").asText(),
        item.get("name").asText(),
        item.get("artists").get(0).get("name").asText(),
        item.get("album").get("name").asText(),
        null, // genre는 platform DTO에만
        item.has("album") && item.get("album").has("images") && item.get("album").get("images").size() > 0
            ? item.get("album").get("images").get(0).get("url").asText() : null,
        item.get("external_urls").get("spotify").asText()
    );
    return SocialMusicTrackDto.fromSpotify(spotifyDto);
  }

  @Override
  public List<SocialMusicTrackDto> getTracks(String accessToken) {
    var response = webClient.get()
        .uri("/me/player/recently-played?limit=20")
        .headers(h -> h.setBearerAuth(accessToken))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .block();

    List<SocialMusicTrackDto> tracks = new ArrayList<>();
    if (response != null && response.has("items")) {
      for (JsonNode play : response.get("items")) {
        JsonNode track = play.get("track");
        SpotifyTrackDto spotifyDto = new SpotifyTrackDto(
            track.get("id").asText(),
            track.get("name").asText(),
            track.get("artists").get(0).get("name").asText(),
            track.get("album").get("name").asText(),
            null,
            track.has("album") && track.get("album").has("images") && track.get("album").get("images").size() > 0
                ? track.get("album").get("images").get(0).get("url").asText() : null,
            track.get("external_urls").get("spotify").asText()
        );
        tracks.add(SocialMusicTrackDto.fromSpotify(spotifyDto));
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
      if (device.has("is_active") && device.get("is_active").asBoolean()) {
        return device.get("name").asText();
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
      if (device.has("is_active") && device.get("is_active").asBoolean()) {
        return true;
      }
    }
    return false;
  }
}
