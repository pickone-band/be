package com.PickOne.domain.user.service;

import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.impl.JpaUserRepositoryImpl;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepositoryImpl userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    @Transactional
    public User updateUser(Long id, User updateData) {
        User current = findById(id);

        User updated = new User(
                current.getId(),
                Optional.ofNullable(updateData.getEmail()).orElse(current.getEmail()),
                current.getPassword(),
                Optional.ofNullable(updateData.getNickname()).orElse(current.getNickname()),
                Optional.ofNullable(updateData.getProfileImage()).orElse(current.getProfileImage()),
                updateData.isPublic(),
                current.isVerified(),
                current.isOauth(),
                current.getRole(),
                Optional.ofNullable(updateData.getInstruments()).orElse(current.getInstruments()),
                Optional.ofNullable(updateData.getGenres()).orElse(current.getGenres())
        );

        return userRepository.save(updated);
    }

    @Transactional
    public void updatePassword(Long id, String rawPassword) {
        User user = findById(id);
        Password newPassword = Password.ofRaw(rawPassword, passwordEncoder);
        userRepository.save(user.changePassword(newPassword));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, boolean onlyPublic, Pageable pageable) {
        return userRepository.search(keyword, onlyPublic, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> findUsersByInstrument(String instrument, Pageable pageable) {
        return userRepository.findAllByInstrument(instrument, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> findUsersByGenre(String genre, Pageable pageable) {
        return userRepository.findAllByGenre(genre, pageable);
    }
}

