package com.PickOne.domain.music.service;

import com.PickOne.domain.music.dto.MusicInfo;
import com.PickOne.domain.music.dto.PlaylistInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyMusicService {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.spotify.com/v1")
            .build();

    public MusicInfo getCurrentlyPlaying(String accessToken) {
        var response = webClient.get()
                .uri("/me/player/currently-playing")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("item")) return null;

        JsonNode item = response.get("item");
        return new MusicInfo(
                item.get("name").asText(),
                item.get("artists").get(0).get("name").asText(),
                item.get("album").get("name").asText(),
                item.get("album").get("images").get(0).get("url").asText(),
                item.get("external_urls").get("spotify").asText()
        );
    }

    public List<PlaylistInfo> getPlaylists(String accessToken) {
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
        return result;
    }

    public String getDeviceName(String accessToken) {
        var response = webClient.get()
                .uri("/me/player/devices")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("devices")) return null;
        for (JsonNode device : response.get("devices")) {
            if (device.get("is_active").asBoolean()) {
                return device.get("name").asText();
            }
        }
        return null;
    }

    public boolean isActiveDevice(String accessToken) {
        var response = webClient.get()
                .uri("/me/player/devices")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("devices")) return false;
        for (JsonNode device : response.get("devices")) {
            if (device.get("is_active").asBoolean()) {
                return true;
            }
        }
        return false;
    }
}