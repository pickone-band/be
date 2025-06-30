package com.pickone.domain.user.model.entity;

import com.pickone.global.common.entity.BaseEntity;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_instruments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInstrumentEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Instrument instrument;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Proficiency proficiency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Builder
  private UserInstrumentEntity(Instrument instrument, Proficiency proficiency) {
    this.instrument = instrument;
    this.proficiency = proficiency;
  }

  public void setUser(UserEntity user) {
    this.user = user;
    if (!user.getInstruments().contains(this)) {
      user.getInstruments().add(this);
    }
  }

  public static UserInstrumentEntity create(UserEntity user, Instrument instrument, Proficiency proficiency) {
    UserInstrumentEntity entity = new UserInstrumentEntity(instrument, proficiency);
    entity.setUser(user); // 양방향 연관관계 설정
    return entity;
  }

  public void updateProficiency(Proficiency newLevel) {
    this.proficiency = newLevel;
  }
}