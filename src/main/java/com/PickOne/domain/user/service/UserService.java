package com.PickOne.domain.user.service;

import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserQueryDslRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserQueryDslRepository userQueryDslRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return UserMapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        UserEntity entity = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        return UserMapper.toDomain(entity);
    }

    @Transactional
    public void updateUser(Long id, User updateData) {
        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        User updated = UserMapper.toDomain(entity).updateWith(updateData);
        entity.updateFromDomain(updated);
    }

    @Transactional
    public void updatePassword(Long id, String rawPassword) {
        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        User domain = UserMapper.toDomain(entity);
        Password newPassword = Password.ofRaw(rawPassword, passwordEncoder);
        entity.updateFromDomain(domain.changePassword(newPassword));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (userJpaRepository.findById(id).isEmpty()) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }
        userJpaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, boolean onlyPublic, Pageable pageable) {
        return userQueryDslRepository.search(keyword, onlyPublic, pageable)
                .map(UserMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<User> findUsersByInstrument(String rawInstrument, Pageable pageable) {
        return userQueryDslRepository.findAllByInstrument(new Instrument(rawInstrument), pageable)
                .map(UserMapper::toDomain);
    }

    @Transactional(readOnly = true)
    public Page<User> findUsersByGenre(String rawGenre, Pageable pageable) {
        return userQueryDslRepository.findAllByGenre(new Genre(rawGenre), pageable)
                .map(UserMapper::toDomain);
    }
}
