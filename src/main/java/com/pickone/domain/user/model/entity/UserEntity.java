package com.pickone.domain.user.model.entity;

import com.pickone.domain.user.model.domain.*;
import com.pickone.domain.user.model.vo.UserAuthInfo;
import com.pickone.domain.user.model.vo.UserPreference;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.model.vo.UserStatus;
import com.pickone.global.common.entity.BaseEntity;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private UserProfile profile;

  @Embedded
  private UserStatus status;

  @Embedded
  private UserPreference preference;

  @Embedded
  private UserAuthInfo authInfo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserInstrumentEntity> instruments = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserMusicEntity> musics = new ArrayList<>();

  @Builder
  private UserEntity(UserProfile profile, UserStatus status,
      UserPreference preference, UserAuthInfo authInfo,
      Role role) {
    this.profile = profile;
    this.status = status;
    this.preference = preference;
    this.authInfo = authInfo;
    this.role = role;
  }

  public static UserEntity of(
      String email,
      String encodedPassword,
      String nickname,
      Gender gender,
      LocalDate birthDate,
      Mbti mbti,
      List<Genre> genres
  ) {
    return new UserEntity(
        UserProfile.of(email, encodedPassword, nickname, birthDate, gender, mbti, null),
        UserStatus.init(),
        UserPreference.ofNullable(genres),
        UserAuthInfo.of(false),
        Role.USER
    );
  }

  public void updatePassword(String encodedNewPassword) {
    this.profile = this.profile.updatePassword(encodedNewPassword);
  }

  public void updatePreference(UserPreference newPreference) {
    this.preference = newPreference;
  }

  public void updateProfile(String nickname, String profileImage, Mbti mbti) {
    this.profile = this.profile.update(nickname, profileImage, mbti);
  }

  public void setInstruments(List<UserInstrumentEntity> newInstruments) {
    // 기존 instruments에 대해 user 참조 끊기
    this.instruments.forEach(instr -> instr.setUser(null));
    this.instruments.clear();

    if (newInstruments != null) {
      newInstruments.forEach(instr -> instr.setUser(this));
      this.instruments.addAll(newInstruments);
    }
  }
  public void verifyEmail() {
    this.status = this.status.verify();
  }


  public void deactivate() {
    this.status = this.status.deactivate(); // UserStatus에 deactivate() 메서드 구현 필요
  }

}
