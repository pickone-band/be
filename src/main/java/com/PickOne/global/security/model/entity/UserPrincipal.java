package com.PickOne.global.security.model.entity;

import com.PickOne.domain.user.model.domain.User;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    // 일반 로그인 생성자
    public UserPrincipal(User user) {
        this(user, Collections.emptyMap());
    }

    // OAuth2 로그인 생성자
    public UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user);
    }

    public static UserPrincipal from(User user, Map<String, Object> attributes) {
        return new UserPrincipal(user, attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword().getValue();
    }

    @Override
    public String getUsername() {
        return user.getEmail().getValue();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail().getValue();
    }

    public Long getUserId() {
        return user.getId();
    }
}