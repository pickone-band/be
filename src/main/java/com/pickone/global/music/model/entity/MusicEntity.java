package com.pickone.global.music.model.entity;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.common.entity.BaseEntity;
import com.pickone.global.music.model.vo.Music;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicEntity extends BaseEntity {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private Music music;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  public static MusicEntity of(Music music, UserEntity user) {
    return new MusicEntity(null, music, user);
  }
}
