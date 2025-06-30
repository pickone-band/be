package com.pickone.domain.user.model.entity;

import com.pickone.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserMusicEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String artist;
  private String album;
  private String imageUrl;
  private String trackUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  public static UserMusicEntity of(
      String title,
      String artist,
      String album,
      String imageUrl,
      String trackUrl,
      UserEntity user
  ) {
    return UserMusicEntity.builder()
        .title(title)
        .artist(artist)
        .album(album)
        .imageUrl(imageUrl)
        .trackUrl(trackUrl)
        .user(user)
        .build();
  }

}