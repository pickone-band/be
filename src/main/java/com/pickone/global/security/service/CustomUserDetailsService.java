package com.pickone.global.security.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.security.model.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserJpaRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // VO 기반 쿼리 네이밍 명확하게
    UserEntity user = userRepository.findByProfileEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없음: " + email));
    return UserPrincipal.from(user);
  }
}
