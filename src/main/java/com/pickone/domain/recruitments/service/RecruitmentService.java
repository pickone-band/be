package com.pickone.domain.recruitments.service;

import com.pickone.domain.recruitments.dto.request.InstrumentProficiencyDto;
import com.pickone.domain.recruitments.dto.request.RecruitmentRequestDto;
import com.pickone.domain.recruitments.dto.response.InstrumentResponseDto;
import com.pickone.domain.recruitments.dto.response.GenreResponseDto;
import com.pickone.domain.recruitments.dto.response.RecruitmentResponseDto;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import com.pickone.domain.recruitments.model.entity.RecruitmentGenre;
import com.pickone.domain.recruitments.model.entity.RecruitmentInstrument;
import com.pickone.domain.recruitments.repository.RecruitmentGenreRepository;
import com.pickone.domain.recruitments.repository.RecruitmentInstrumentRepository;
import com.pickone.domain.recruitments.repository.RecruitmentRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        Recruitment recruitment = recruitmentRepository.save(requestDto.toEntity(user));

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

        if (!recruitment.getUser().getId().equals(userId)) {
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

        if (!recruitment.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_RECRUITMENT_ACCESS);
        }
        recruitmentGenreRepository.deleteAllByRecruitmentId(recruitmentId);
        recruitmentInstrumentRepository.deleteAllByRecruitmentId(recruitmentId);
        recruitmentRepository.deleteById(recruitmentId);
    }
}