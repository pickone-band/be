// UserServiceImpl.java
package com.PickOne.user.service;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.user.model.domain.Email;
import com.PickOne.user.model.domain.Password;
import com.PickOne.user.model.domain.User;
import com.PickOne.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "해당 ID로 등록된 사용자를 찾을 수 없습니다: " + id,
                        ErrorCode.USER_INFO_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        "해당 이메일로 등록된 사용자를 찾을 수 없습니다: " + email,
                        ErrorCode.USER_INFO_NOT_FOUND));
    }
}