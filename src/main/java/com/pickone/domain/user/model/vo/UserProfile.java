package com.pickone.domain.user.model.vo;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.global.common.enums.Mbti;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfile {

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true)
  private String nickname;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Gender gender;

  @Enumerated(EnumType.STRING)
  @Column
  private Mbti mbti;

  @Column(name = "profile_image")
  private String profileImage;

  public static UserProfile of(String email, String password, String nickname, LocalDate birthDate,
      Gender gender, Mbti mbti, String profileImage) {
    return new UserProfile(email, password, nickname, birthDate, gender, mbti, profileImage);
  }

  public UserProfile update(String newNickname, String newProfileImage, Mbti newMbti) {
    return new UserProfile(
        this.email,
        this.password,
        newNickname != null ? newNickname : this.nickname,
        this.birthDate,
        this.gender,
        newMbti != null ? newMbti : this.mbti,
        newProfileImage != null ? newProfileImage : this.profileImage
    );
  }

  public UserProfile updatePassword(String newEncodedPassword) {
    return new UserProfile(
        this.email,
        newEncodedPassword,
        this.nickname,
        this.birthDate,
        this.gender,
        this.mbti,
        this.profileImage
    );
  }
}