package com.pickone.domain.userPerformance.service;

import com.pickone.domain.user.dto.UserResponse;
import com.pickone.domain.user.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getUsersFrom1To10() {
        return userRepository.findByIdBetween(1L, 10L).stream()
                .map(userMapper::toDto)
                .toList();
    }

}
