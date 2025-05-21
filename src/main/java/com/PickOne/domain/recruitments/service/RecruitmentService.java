package com.PickOne.domain.recruitments.service;

import com.PickOne.domain.recruitments.dto.request.InstrumentProficiencyDto;
import com.PickOne.domain.recruitments.dto.request.RecruitmentRequestDto;
import com.PickOne.domain.recruitments.dto.response.InstrumentResponseDto;
import com.PickOne.domain.recruitments.dto.response.GenreResponseDto;
import com.PickOne.domain.recruitments.dto.response.RecruitmentResponseDto;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.recruitments.model.entity.RecruitmentGenre;
import com.PickOne.domain.recruitments.model.entity.RecruitmentInstrument;
import com.PickOne.domain.recruitments.repository.RecruitmentGenreRepository;
import com.PickOne.domain.recruitments.repository.RecruitmentInstrumentRepository;
import com.PickOne.domain.recruitments.repository.RecruitmentRepository;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.JpaUserRepositoryImpl;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.domain.user.service.UserServiceImpl;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.security.model.entity.SecurityUser;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final RecruitmentInstrumentRepository recruitmentInstrumentRepository;
    private final RecruitmentGenreRepository recruitmentGenreRepository;
    private final UserJpaRepository userJpaRepository;

    /**
     * 모집공고 등록
     */
    @Transactional
    public Long registerRecruitment(RecruitmentRequestDto requestDto, Long userId) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        Recruitment recruitment = recruitmentRepository.save(requestDto.toEntity(userEntity));

        List<InstrumentProficiencyDto> ipDtoList = requestDto.getInstrumentProficiencyDto();
        List<RecruitmentInstrument> allInstruments = ipDtoList.stream()
                .flatMap(ipDto -> ipDto.toEntityList(recruitment).stream())
                .collect(Collectors.toList());

        List<RecruitmentGenre> recruitmentGenres = requestDto.getGenreRequestDto()
                .getRecruitmentGenres().stream()
                .map(genre -> new RecruitmentGenre(recruitment, genre))
                .collect(Collectors.toList());

        recruitmentGenreRepository.saveAll(recruitmentGenres);
        recruitmentInstrumentRepository.saveAll(allInstruments);

        return recruitment.getId();
    }

    /**
     * /** 모집공고 단건 조회
     */
    public RecruitmentResponseDto getRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("모집공고를 찾을 수 없습니다. ID: " + recruitmentId));

        List<RecruitmentInstrument> recruitmentInstruments =
                recruitmentInstrumentRepository.findAllByRecruitmentId(recruitmentId);

        List<InstrumentResponseDto> instruments = recruitmentInstruments.stream()
                .map(InstrumentResponseDto::from)
                .collect(Collectors.toList());

        List<RecruitmentGenre> genreEntities = recruitmentGenreRepository.findAllByRecruitmentId(recruitmentId);
        GenreResponseDto genreDto = GenreResponseDto.from(genreEntities);

        return RecruitmentResponseDto.builder()
                .id(recruitment.getId())
                .type(recruitment.getType())
                .visibility(recruitment.getVisibility())
                .title(recruitment.getTitle())
                .description(recruitment.getDescription())
                .region(recruitment.getRegion())
                .snsLink(recruitment.getSnsLink())
                .thumbnail(recruitment.getThumbnail())
                .instruments(instruments)
                .genres(genreDto)
                .build();
    }

    public Page<RecruitmentResponseDto> getRecruitments(Pageable pageable) {
        Page<Recruitment> page = recruitmentRepository.findAll(pageable);
        return page.map(recruitment -> {
            List<RecruitmentInstrument> recruitmentInstruments =
                    recruitmentInstrumentRepository.findAllByRecruitmentId(recruitment.getId());

            List<InstrumentResponseDto> instruments = recruitmentInstruments.stream()
                    .map(InstrumentResponseDto::from)
                    .collect(Collectors.toList());

            List<RecruitmentGenre> genreEntities = recruitmentGenreRepository.findAllByRecruitmentId(
                    recruitment.getId());
            GenreResponseDto genreDto = GenreResponseDto.from(genreEntities);

            return RecruitmentResponseDto.of(recruitment, instruments, genreDto);
        });
    }

    @Transactional
    public Long modifyRecruitment(RecruitmentRequestDto requestDto, Long recruitmentId, Long userId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RECRUITMENT_ID));

        if (!recruitment.getUserEntity().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_RECRUITMENT_ACCESS);
        }
        System.out.println("userId: " + userId);
        recruitment.update(requestDto);
        // 회원이 기존 세션구성을 변경하는 경우.
        if (requestDto.getInstrumentProficiencyDto() != null) {
            recruitmentInstrumentRepository.deleteAllByRecruitmentId(recruitmentId);
            List<InstrumentProficiencyDto> ipDtoList = requestDto.getInstrumentProficiencyDto();
            List<RecruitmentInstrument> allInstruments = ipDtoList.stream()
                    .flatMap(ipDto -> ipDto.toEntityList(recruitment).stream())
                    .collect(Collectors.toList());
            recruitmentInstrumentRepository.saveAll(allInstruments);
        }
        //장르를 변경하는 경우
        if (requestDto.getGenreRequestDto() != null) {
            recruitmentGenreRepository.deleteAllByRecruitmentId(recruitmentId);
            List<RecruitmentGenre> recruitmentGenres = requestDto.getGenreRequestDto()
                    .getRecruitmentGenres().stream()
                    .map(genre -> new RecruitmentGenre(recruitment, genre))
                    .collect(Collectors.toList());
            recruitmentGenreRepository.saveAll(recruitmentGenres);
        }

        return recruitment.getId();
    }
    @Transactional(readOnly = false)
    public void deleteRecruitment(Long recruitmentId, Long userId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_RECRUITMENT_ID));

        if (!recruitment.getUserEntity().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_RECRUITMENT_ACCESS);
        }
        recruitmentGenreRepository.deleteAllByRecruitmentId(recruitmentId);
        recruitmentInstrumentRepository.deleteAllByRecruitmentId(recruitmentId);
        recruitmentRepository.deleteById(recruitmentId);
    }
}