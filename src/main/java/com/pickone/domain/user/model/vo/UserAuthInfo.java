package com.pickone.domain.user.model.vo;

import com.pickone.domain.user.model.domain.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter // 임시
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuthInfo {
  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private AuthProvider provider;
  private String providerId;

  public static UserAuthInfo of(String password, AuthProvider provider, String providerId) {
    return new UserAuthInfo(password, provider, providerId);
  }
}
