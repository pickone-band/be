package com.PickOne.global.security.service;

import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.security.model.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        return userJpaRepository.findByEmail(email)
                .map(UserMapper::toDomain)
                .map(UserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자가 없습니다: " + email));
    }
}
