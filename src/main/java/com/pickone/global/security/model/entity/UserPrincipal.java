package com.pickone.global.security.model.entity;

import com.pickone.domain.user.model.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

  private final UserEntity user;
  private final Map<String, Object> attributes;

  public UserPrincipal(UserEntity user) {
    this(user, Collections.emptyMap());
  }

  public UserPrincipal(UserEntity user, Map<String, Object> attributes) {
    this.user = user;
    this.attributes = attributes;
  }

  public static UserPrincipal from(UserEntity user) {
    return new UserPrincipal(user);
  }

  public static UserPrincipal from(UserEntity user, Map<String, Object> attributes) {
    return new UserPrincipal(user, attributes);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> "ROLE_" + user.getRole().name());
  }

  @Override
  public String getPassword() {
    return user.getProfile().getPassword();
  }

  @Override
  public String getUsername() {
    return user.getProfile().getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return user.getStatus().isActive();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !user.getStatus().isLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    LocalDateTime expiredAt = user.getStatus().getCredentialsExpiredAt();
    return expiredAt == null || expiredAt.isAfter(LocalDateTime.now());
  }

  @Override
  public boolean isEnabled() {
    return user.getStatus().isVerified();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return user.getProfile().getEmail();
  }

  public Long getUserId() {
    return user.getId();
  }
}
