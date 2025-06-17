package com.PickOne.domain.user.service;

import com.PickOne.domain.user.dto.UserSearchConditionDto;
import com.PickOne.domain.user.dto.UserUpdateRequestDto;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.model.entity.UserInstrumentEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserQueryDslRepository;
import com.PickOne.global.common.enums.Genre;
import com.PickOne.global.common.enums.Instrument;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final UserQueryDslRepository userQueryDslRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    @Transactional
    public void updateUser(Long id, UserUpdateRequestDto dto) {
        UserEntity user = findById(id);

        user.updateNickname(dto.nickname());
        user.updateProfileImage(dto.profileImageUrl());
        user.updateVisibility(dto.isPublic());
        user.updateMbti(dto.mbti());

        if (dto.genres() != null) {
            List<Genre> genres = new ArrayList<>(dto.genres());
            user.updateGenres(genres);
        }

        if (dto.instruments() != null) {
            List<UserInstrumentEntity> instrumentEntities = dto.instruments().stream()
                    .map(i -> UserInstrumentEntity.builder()
                            .instrument(i.instrument())
                            .proficiency(i.proficiency())
                            .build())
                    .collect(Collectors.toList());
            user.updateInstruments(instrumentEntities);
        }
    }

    @Transactional
    public void updatePassword(Long id, String rawPassword) {
        UserEntity user = findById(id);
        String encoded = passwordEncoder.encode(rawPassword);
        user.updatePassword(encoded);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userJpaRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        }
        userJpaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> searchUsers(UserSearchConditionDto condition, Pageable pageable) {
        return userQueryDslRepository.searchUsers(condition, pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> findUsersByInstrument(String rawInstrument, Pageable pageable) {
        Instrument instrument = Instrument.valueOf(rawInstrument);
        return userQueryDslRepository.searchByInstrument(instrument, pageable);
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> findUsersByGenre(String rawGenre, Pageable pageable) {
        Genre genre = Genre.valueOf(rawGenre);
        return userQueryDslRepository.searchByGenre(genre, pageable);
    }
}
