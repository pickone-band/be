package com.pickone.global.security.entity;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.model.vo.*;
import com.pickone.domain.user.model.domain.Role;
import java.security.Principal;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User, Principal {
  private final Long id;
  private final String email;
  private final String password;
  private final String nickname;
  private final Role role;

  private final boolean isActive;
  private final boolean isLocked;
  private final boolean isVerified;
  private final LocalDateTime credentialsExpiredAt;

  // 프로필/상태/선호/인증/보안 정보
  private final UserProfile profile;
  private final UserStatus status;
  private final UserPreference preference;
  private final UserAuthInfo authInfo;
  private final SecurityInfo securityInfo;

  // OAuth2User 인터페이스 필드
  private final Map<String, Object> attributes;

  @Builder // builder 내부 protected 또는 package-private 권장
  public UserPrincipal(Long id,
      String email,
      String password,
      String nickname,
      Role role,
      boolean isActive,
      boolean isLocked,
      boolean isVerified,
      LocalDateTime credentialsExpiredAt,
      UserProfile profile,
      UserStatus status,
      UserPreference preference,
      UserAuthInfo authInfo,
      SecurityInfo securityInfo,
      Map<String, Object> attributes // 추가
  ) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.role = role;
    this.isActive = isActive;
    this.isLocked = isLocked;
    this.isVerified = isVerified;
    this.credentialsExpiredAt = credentialsExpiredAt;
    this.profile = profile;
    this.status = status;
    this.preference = preference;
    this.authInfo = authInfo;
    this.securityInfo = securityInfo;
    this.attributes = attributes != null ? attributes : Collections.emptyMap();
  }

  public static UserPrincipal from(User user) {
    return from(user, null);
  }

  public static UserPrincipal from(User user, Map<String, Object> attributes) {
    return UserPrincipal.builder()
        .id(user.getId())
        .email(user.getProfile().getEmail())
        .password(user.getAuthInfo().getPassword())
        .nickname(user.getProfile().getNickname())
        .role(user.getRole())
        .isActive(user.getStatus().isActive())
        .isLocked(user.getStatus().isLocked())
        .isVerified(user.getStatus().isVerified())
        .credentialsExpiredAt(user.getStatus().getCredentialsExpiredAt())
        .profile(user.getProfile())
        .status(user.getStatus())
        .preference(user.getPreference())
        .authInfo(user.getAuthInfo())
        .securityInfo(user.getSecurityInfo())
        .attributes(attributes)
        .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList((GrantedAuthority) () -> role.name());
  }

  @Override
  public String getUsername() { return email; }

  @Override
  public boolean isAccountNonExpired() {
    return credentialsExpiredAt == null || credentialsExpiredAt.isAfter(LocalDateTime.now());
  }

  @Override
  public boolean isAccountNonLocked() {
    return !isLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return isAccountNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return isActive && isVerified;
  }

  // OAuth2User 인터페이스 구현
  @Override
  public Map<String, Object> getAttributes() {
    return attributes != null ? attributes : Collections.emptyMap();
  }

  @Override
  public String getName() {
    return String.valueOf(id); // ✅ 이메일 말고 ID만 반환
  }

}
