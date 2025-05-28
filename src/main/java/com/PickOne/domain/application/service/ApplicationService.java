package com.PickOne.domain.application.service;

import com.PickOne.domain.application.dto.request.ApplicationRequestDto;
import com.PickOne.domain.application.model.ApplicationStatus;
import com.PickOne.domain.application.model.entity.Application;
import com.PickOne.domain.application.repository.ApplicationRepository;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.recruitments.repository.RecruitmentRepository;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserJpaRepository userJpaRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    public Long applyToRecruitment(Long userId, Long recruitmentId, ApplicationRequestDto requestDto) {
        // 사용자 조회
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        // 모집글 조회
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RECRUITMENT_ID));

        // 중복 신청 방지
        if (applicationRepository.existsByUserEntityAndRecruitment(userEntity, recruitment)) {
            throw new BusinessException(ErrorCode.DUPLICATE_APPLICATION);
        }
        Application application=applicationRepository.save(requestDto.toEntity(userEntity,recruitment));

        return application.getId();
    }

}

