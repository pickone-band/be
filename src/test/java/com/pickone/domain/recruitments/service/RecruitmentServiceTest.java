//package com.pickone.domain.recruitments.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//import com.pickone.domain.recruitments.dto.request.GenreRequestDto;
//import com.pickone.domain.recruitments.dto.request.InstrumentProficiencyDto;
//import com.pickone.domain.recruitments.dto.request.RecruitmentRequestDto;
//import com.pickone.domain.recruitments.dto.response.RecruitmentResponseDto;
//import com.pickone.domain.user.model.domain.Gender;
//import com.pickone.domain.user.model.domain.Role;
//import com.pickone.global.common.enums.Genre;
//import com.pickone.global.common.enums.Instrument;
//import com.pickone.global.common.enums.Proficiency;
//import com.pickone.domain.recruitments.model.Status;
//import com.pickone.domain.recruitments.model.Type;
//import com.pickone.domain.recruitments.model.Visibility;
//import com.pickone.domain.user.model.entity.UserEntity;
//import com.pickone.domain.user.repository.UserJpaRepository;
//import com.pickone.global.exception.BusinessException;
//import com.pickone.global.exception.ErrorCode;
//import jakarta.transaction.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//
//@Disabled("Disabled temporarily due to H2-related tests.")
//@SpringBootTest
//@Transactional
//class RecruitmentServiceTest {
//
//    @MockBean
//    private ClientRegistrationRepository clientRegistrationRepository;
//
//    @MockBean
//    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
//
//    @Autowired
//    private RecruitmentService recruitmentService;
//
//    @Autowired
//    private UserJpaRepository userJpaRepository;
//
//    RecruitmentRequestDto requestDto = RecruitmentRequestDto.builder()
//            .type(Type.Once)
//            .status(Status.Recruiting)
//            .visibility(Visibility.PUBLIC)
//            .title("테스트 모집공고1")
//            .description("우리는 주말마다 연습하는 인디 밴드입니다.")
//            .region("서울")
//            .thumbnail("https://www.schezade.co.kr/board/guide/upload/guide_45_3.jpg")
//            .snsLink("인스타주소")
//            .instrumentProficiencyDto(List.of(
//                    new InstrumentProficiencyDto(List.of(
//                            InstrumentProficiencyDto.InstrumentDetail.builder()
//                                    .instrument(Instrument.ELECTRIC_GUITAR)
//                                    .proficiency(Proficiency.ADVANCED)
//                                    .build(),
//                            InstrumentProficiencyDto.InstrumentDetail.builder()
//                                    .instrument(Instrument.BASS)
//                                    .proficiency(Proficiency.INTERMEDIATE)
//                                    .build()
//                    ))
//            ))
//            .genreRequestDto(
//                    new GenreRequestDto(
//                            List.of(
//                                    Genre.INDIE_ROCK,
//                                    Genre.SHOEGAZING
//                            )
//                    )
//            )
//            .build();
//
//    @Test
//    void 모집공고_등록_성공_테스트() {
//        // given - 도메인 객체 생성 및 저장
//        UserEntity testUser = createTestUser("test@example.com", "테스트유저");
//        UserEntity savedUser = userJpaRepository.save(testUser);
//
//        //when - 저장된 유저 ID를 통해 모집공고 등록
//        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser.getId());
//        // then
//        assertNotNull(savedId);
//        System.out.println("등록된 ID: " + savedId);
//    }
//
//    @Test
//    void 모집공고_단건_조회_테스트() {
//        UserEntity savedUser = userJpaRepository.save(createTestUser("test@example.com", "테스트유저"));
//        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser.getId());
//        RecruitmentResponseDto responseDto = recruitmentService.getRecruitment(savedId);
//        assertEquals(responseDto.getGenres().getGenre(), List.of(Genre.INDIE_ROCK, Genre.SHOEGAZING));
//    }
//
//    @Test
//    void 모집공고_수정_테스트() {
//        UserEntity savedUser = userJpaRepository.save(createTestUser("test@example.com", "테스트유저"));
//        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser.getId());
//
//        RecruitmentRequestDto modifyDto = RecruitmentRequestDto.builder()
//                .type(Type.Once)
//                .status(Status.Recruiting)
//                .visibility(Visibility.PUBLIC)
//                .title("수정된 모집공고 제목")
//                .description("수정된 내용")
//                .region("경기도")
//                .instrumentProficiencyDto(List.of(
//                        new InstrumentProficiencyDto(List.of(
//                                InstrumentProficiencyDto.InstrumentDetail.builder()
//                                        .instrument(Instrument.DRUMS)
//                                        .proficiency(Proficiency.INTERMEDIATE)
//                                        .build()
//                        ))
//                ))
//                .genreRequestDto(
//                        new GenreRequestDto(
//                                List.of(
//                                        Genre.POST_ROCK
//                                )
//                        )
//                )
//                .build();
//        recruitmentService.modifyRecruitment(modifyDto, savedId, 1L);
//        RecruitmentResponseDto response = recruitmentService.getRecruitment(savedId);
//        assertEquals("수정된 모집공고 제목", response.getTitle());
//        assertEquals(List.of(Genre.POST_ROCK), response.getGenres().getGenre());
//
//    }
//
//    @Test
//    void 모집공고_삭제_테스트() {
//        UserEntity savedUser1 = userJpaRepository.save(createTestUser("test@example.com", "테스트유저"));  // ✅
//        Long savedId = recruitmentService.registerRecruitment(requestDto, savedUser1.getId());
//
//        UserEntity savedUser2 = userJpaRepository.save(createTestUser("test2@example.com", "다른유저"));
//        // when & then
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            recruitmentService.deleteRecruitment(savedId, savedUser2.getId());
//        });
//
//        assertEquals(ErrorCode.UNAUTHORIZED_RECRUITMENT_ACCESS, exception.getErrorCode());
//    }
//
//  private UserEntity createTestUser(String email, String nickname) {
//    return UserEntity.of(
//        email,
//        "encoded-password",
//        nickname,
//        Gender.MALE,
//        LocalDate.of(1995, 1, 1),
//        null,           // Mbti: 테스트에서 null 가능 (또는 원하는 값으로)
//        List.of()       // genres
//    );
//  }
//}
