package com.PickOne.domain.recruitments.service;

import static org.junit.jupiter.api.Assertions.*;

import com.PickOne.domain.recruitments.dto.request.InstrumentProficiencyDto;
import com.PickOne.domain.recruitments.dto.request.GenreRequestDto;
import com.PickOne.domain.recruitments.dto.request.RecruitmentRequestDto;
import com.PickOne.domain.recruitments.dto.response.RecruitmentResponseDto;
import com.PickOne.domain.recruitments.model.Genre;
import com.PickOne.domain.recruitments.model.Instrument;
import com.PickOne.domain.recruitments.model.Proficiency;
import com.PickOne.domain.recruitments.model.Status;
import com.PickOne.domain.recruitments.model.Type;
import com.PickOne.domain.recruitments.model.Visibility;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class RecruitmentServiceTest {

    @Autowired
    private RecruitmentService recruitmentService;

    @Autowired
    private UserRepository userRepository;

    RecruitmentRequestDto requestDto = RecruitmentRequestDto.builder()
            .type(Type.Once)
            .status(Status.Recruiting)
            .visibility(Visibility.PUBLIC)
            .title("테스트 모집공고1")
            .description("우리는 주말마다 연습하는 인디 밴드입니다.")
            .region("서울")
            .thumbnail("https://www.schezade.co.kr/board/guide/upload/guide_45_3.jpg")
            .snsLink("인스타주소")
            .instrumentProficiencyDto(List.of(
                    new InstrumentProficiencyDto(List.of(
                            InstrumentProficiencyDto.InstrumentDetail.builder()
                                    .instrument(Instrument.ELECTRIC_GUITAR)
                                    .proficiency(Proficiency.ADVANCED)
                                    .build(),
                            InstrumentProficiencyDto.InstrumentDetail.builder()
                                    .instrument(Instrument.BASS)
                                    .proficiency(Proficiency.INTERMEDIATE)
                                    .build()
                    ))
            ))
            .genreRequestDto(
                    new GenreRequestDto(
                            List.of(
                                    Genre.INDIE_ROCK,
                                    Genre.SHOEGAZING
                            )
                    )
            )
            .build();

    @Test
    void 모집공고_등록_성공_테스트() {
        // given - 도메인 객체 생성 및 저장
        User testUser = User.of(
                null,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded-password")
        );
        User savedUser = userRepository.save(testUser);

        //when - 저장된 유저 ID를 통해 모집공고 등록
        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser.getId());
        // then
        assertNotNull(savedId);
        System.out.println("등록된 ID: " + savedId);
    }

    @Test
    void 모집공고_단건_조회_테스트() {
        User testUser = User.of(
                null,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded-password")
        );
        User savedUser = userRepository.save(testUser);
        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser.getId());
        RecruitmentResponseDto responseDto = recruitmentService.getRecruitment(savedId);
        assertEquals(responseDto.getGenres().getGenre(), List.of(Genre.INDIE_ROCK, Genre.SHOEGAZING));
    }

    @Test
    void 모집공고_수정_테스트() {
        User testUser = User.of(
                null,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded-password")
        );
        User savedUser = userRepository.save(testUser);
        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser.getId());

        RecruitmentRequestDto modifyDto = RecruitmentRequestDto.builder()
                .type(Type.Once)
                .status(Status.Recruiting)
                .visibility(Visibility.PUBLIC)
                .title("수정된 모집공고 제목")
                .description("수정된 내용")
                .region("경기도")
                .instrumentProficiencyDto(List.of(
                        new InstrumentProficiencyDto(List.of(
                                InstrumentProficiencyDto.InstrumentDetail.builder()
                                        .instrument(Instrument.DRUMS)
                                        .proficiency(Proficiency.INTERMEDIATE)
                                        .build()
                        ))
                ))
                .genreRequestDto(
                        new GenreRequestDto(
                                List.of(
                                        Genre.POST_ROCK
                                )
                        )
                )
                .build();
        recruitmentService.modifyRecruitment(modifyDto, savedId, 1L);
        RecruitmentResponseDto response = recruitmentService.getRecruitment(savedId);
        assertEquals("수정된 모집공고 제목", response.getTitle());
        assertEquals(List.of(Genre.POST_ROCK), response.getGenres().getGenre());

    }

    @Test
    void 모집공고_삭제_테스트() {
        User testUser = User.of(
                null,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded-password")
        );
        User savedUser = userRepository.save(testUser);
        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser.getId());

        User testUser2 = User.of(
                null,
                Email.of("test2@example.com"),
                Password.ofEncoded("encoded-password")
        );
        User savedUser2 = userRepository.save(testUser2);
        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            recruitmentService.deleteRecruitment(savedId, savedUser2.getId());
        });

        assertEquals(ErrorCode.UNAUTHORIZED_RECRUITMENT_ACCESS, exception.getErrorCode());
    }

}
