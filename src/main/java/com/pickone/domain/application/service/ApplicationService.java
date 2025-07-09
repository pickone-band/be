package com.pickone.domain.application.service;

import com.pickone.domain.application.dto.request.ApplicationRequestDto;
import com.pickone.domain.application.dto.response.ApplicationResponseDto;
import com.pickone.domain.application.model.ApplicationStatus;
import com.pickone.domain.application.model.entity.Application;
import com.pickone.domain.application.repository.ApplicationRepository;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import com.pickone.domain.recruitments.repository.RecruitmentRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
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
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        // 모집글 조회
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RECRUITMENT_ID));

        // 중복 신청 방지
        if (applicationRepository.existsByUserAndRecruitment(user, recruitment)) {
            throw new BusinessException(ErrorCode.DUPLICATE_APPLICATION);
        }
        Application application=applicationRepository.save(requestDto.toEntity(user,recruitment));

        return application.getId();
    }


    @Transactional
    public ApplicationResponseDto getMyApplication(Long userId, Long recruitmentId) {
        User user= userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        Recruitment recruitment =recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RECRUITMENT_ID));
        Application application =applicationRepository.findByUserAndRecruitment(user,recruitment)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_INFO_NOT_FOUND));

        return ApplicationResponseDto.builder()
                .recruitmentId(recruitment.getId())
                .title(recruitment.getTitle())
                .region(recruitment.getRegion())
                .thumbnail(recruitment.getThumbnail())
                .createdAt(recruitment.getCreatedAt().toString())  // 필요 시 포맷팅

                .message(application.getMessage())
                .portfolioUrl(application.getPortfolioUrl())
                .applicantThumbnail(application.getThumbnail())
                .mbti(application.getMbti())
                .instrument(application.getInstrument())
                .proficiency(application.getProficiency())
                .status(application.getStatus())
                .build();
    }

    @Transactional
    public void modifyApplication(Long userId, Long recruitmentId, ApplicationRequestDto requestDto) {
        User user=userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        Recruitment recruitment =recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RECRUITMENT_ID));
        Application application =applicationRepository.findByUserAndRecruitment(user,recruitment)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_INFO_NOT_FOUND));

        application.update(requestDto);
    }

    @Transactional
    public void cancelMyApplication(Long userId, Long recruitmentId) {
        User user=userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
        Recruitment recruitment =recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RECRUITMENT_ID));
        Application application =applicationRepository.findByUserAndRecruitment(user,recruitment)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_INFO_NOT_FOUND));

        if (application.getStatus() == ApplicationStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 신청입니다.");
        }

        application.changeStatus(ApplicationStatus.CANCELED);
    }
}

