package com.pickone.global.music.model.vo;

import com.pickone.global.music.dto.SocialMusicTrackDto;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Music {
  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String artist;

  @Column
  private String album;

  @Column
  private String genre;

  @Column(nullable = false)
  private String platformTrackId;

  // 정적 팩토리 메서드 (파라미터 순서에 주의)
  public static Music of(String title, String artist, String album, String genre, String platformTrackId) {
    return new Music(title, artist, album, genre, platformTrackId);
  }

  // DTO → VO 변환: DTO의 각 필드 → Music의 필드에 정확하게 매핑
  public static Music from(SocialMusicTrackDto dto) {
    return new Music(
        dto.title(),
        dto.artist(),
        dto.album(),
        dto.genre(),
        dto.platformTrackId()
    );
  }
}
