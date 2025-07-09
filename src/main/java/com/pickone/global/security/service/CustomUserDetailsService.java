package com.pickone.global.security.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.security.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserJpaRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.debug("[CustomUserDetailsService] 사용자 조회 시작: email={}", email);

    return userRepository.findByProfileEmail(email)
        .map(UserPrincipal::from)
        .orElseThrow(() -> {
          log.warn("[CustomUserDetailsService] 사용자 찾을 수 없음: {}", email);
          return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        });
  }
}