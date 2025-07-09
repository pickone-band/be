package com.pickone.domain.user.model.vo;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.global.common.enums.Mbti;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile {
  @Column(nullable = false)
  private String nickname;
  @Column(nullable = false)
  private String email;
  @Column(nullable = false)
  private LocalDate birthDate;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Gender gender;
  @Enumerated(EnumType.STRING)
  private Mbti mbti;
  @Column
  private String profileImageUrl;
  @Column(length = 1000) // 길이 제한 원하는 대로 조절
  private String introduction; // 👈 추가된 필드

  public static UserProfile of(String nickname, String email, LocalDate birthDate, Gender gender, Mbti mbti, String profileImageUrl, String introduction) {
    return new UserProfile(nickname, email, birthDate, gender, mbti, profileImageUrl, introduction);
  }
}
