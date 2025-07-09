package com.pickone.global.music.model.vo;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicInfo {
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

  public static MusicInfo of(String title, String artist, String album, String genre, String platformTrackId) {
    return new MusicInfo(title, artist, album, genre, platformTrackId);
  }
}

