package com.pickone.global.music.entity;

import com.pickone.domain.user.entity.User;
import com.pickone.global.common.entity.BaseEntity;
import com.pickone.global.music.model.vo.MusicInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Music extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private MusicInfo musicInfo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  public static Music create(MusicInfo musicInfo, User user) {
    return new Music(null, musicInfo, user);
  }
}
