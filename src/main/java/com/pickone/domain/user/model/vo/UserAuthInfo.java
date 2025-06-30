package com.pickone.domain.user.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserAuthInfo {

  @Column(name = "is_oauth", nullable = false)
  private boolean isOauth;

//  @Column(name = "is_verified", nullable = false)
//  private boolean isVerified;

  public static UserAuthInfo of(boolean isOauth) {
    return new UserAuthInfo(isOauth);
  }
}
