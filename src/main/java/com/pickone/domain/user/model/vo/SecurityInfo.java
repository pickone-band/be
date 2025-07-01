package com.pickone.domain.user.model.vo;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SecurityInfo {
  @Column(nullable = false)
  private boolean twoFactorEnabled;
  private String twoFactorSecret;

  public static SecurityInfo of(boolean twoFactorEnabled, String twoFactorSecret) {
    return new SecurityInfo(twoFactorEnabled, twoFactorSecret);
  }
}
