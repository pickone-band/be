package com.PickOne.domain.music.service;

import com.PickOne.domain.music.dto.MusicInfo;
import com.PickOne.domain.music.dto.PlaylistInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleMusicService {
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://www.googleapis.com/youtube/v3")
            .build();

    public MusicInfo getCurrentlyPlaying(String accessToken) {
        // 유튜브에서는 현재 재생 중 트랙 조회 API가 없음
        return null;
    }

    public List<PlaylistInfo> getPlaylists(String accessToken) {
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
                        0 // 유튜브 API에서는 track 개수 제공 안함
                ));
            }
        }
        return result;
    }

    public String getDeviceName(String accessToken) {
        return "YouTube Web"; // 유튜브에서는 디바이스 API 제공되지 않음
    }

    public boolean isActiveDevice(String accessToken) {
        return false; // 유튜브는 현재 디바이스 재생 정보 없음
    }
}