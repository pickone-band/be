package com.PickOne.term.controller;

import com.PickOne.term.controller.dto.terms.CreateTermsRequest;
import com.PickOne.term.controller.dto.terms.UpdateTermsContentRequest;
import com.PickOne.term.model.domain.*;
import com.PickOne.term.repository.terms.TermsRepository;
import com.PickOne.term.service.TermsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 약관 관련 컨트롤러 통합 테스트
 * 참고: 시큐리티 설정이 필요하지만 아직 설정되지 않은 상황을 고려하여
 * 필요한 곳에 @WithMockUser 어노테이션을 추가하고 시큐리티 관련 설정은 주석으로 표시
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TermsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private TermsService termsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Terms testTerms;
    private Terms requiredTerms;

    @BeforeEach
    void setUp() {
        // 테스트용 약관 데이터 생성 - 팩토리 메서드 사용
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // 서비스 이용약관 생성
        testTerms = Terms.create(
                Title.of("서비스 이용약관"),
                Content.of("서비스 이용약관 내용입니다."),
                TermsType.SERVICE,
                Version.of("1.0.0"),
                EffectiveDate.of(today),
                Required.of(false)
        );
        // DB에 저장
        testTerms = termsRepository.save(testTerms);

        // 개인정보 처리방침 생성
        requiredTerms = Terms.create(
                Title.of("개인정보 처리방침"),
                Content.of("개인정보 처리방침 내용입니다."),
                TermsType.PRIVACY,
                Version.of("1.0.0"),
                EffectiveDate.of(today),
                Required.of(true)
        );
        // DB에 저장
        requiredTerms = termsRepository.save(requiredTerms);
    }

    @AfterEach
    void tearDown() {
        termsRepository.deleteAll();
    }

    @Test
    @DisplayName("ID로 약관 조회 통합 테스트")
    void getTermsById_ShouldReturnTerms() throws Exception {
        // When
        ResultActions result = mockMvc.perform(get("/api/terms/{id}", testTerms.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTerms.getId()))
                .andExpect(jsonPath("$.title.value").value(testTerms.getTitle().getValue()))
                .andExpect(jsonPath("$.content.value").value(testTerms.getContent().getValue()))
                .andExpect(jsonPath("$.type").value(testTerms.getType().toString()));
    }

    @Test
    @DisplayName("필수 약관 목록 조회 통합 테스트")
    void getAllRequiredTerms_ShouldReturnRequiredTermsList() throws Exception {
        // When
        ResultActions result = mockMvc.perform(get("/api/terms/required")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(requiredTerms.getId()))
                .andExpect(jsonPath("$[0].title.value").value(requiredTerms.getTitle().getValue()))
                .andExpect(jsonPath("$[0].required.value").value(true));
    }

    @Test
    @DisplayName("특정 유형의 최신 약관 조회 통합 테스트")
    void getLatestTermsByType_ShouldReturnLatestTerms() throws Exception {
        // Given: 새로운, 더 높은 버전의 약관 추가
        Terms newVersionTerms = Terms.create(
                Title.of("서비스 이용약관 (신규)"),
                Content.of("서비스 이용약관 내용입니다. (업데이트)"),
                TermsType.SERVICE,
                Version.of("1.1.0"),
                EffectiveDate.of(LocalDate.now().plusDays(10)),
                Required.of(false)
        );
        newVersionTerms = termsRepository.save(newVersionTerms);

        // When
        ResultActions result = mockMvc.perform(get("/api/terms/latest")
                .param("type", "SERVICE")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newVersionTerms.getId()))
                .andExpect(jsonPath("$.title.value").value(newVersionTerms.getTitle().getValue()))
                .andExpect(jsonPath("$.version.value").value("1.1.0"));
    }

    @Test
    @DisplayName("현재 유효한 약관 조회 통합 테스트")
    void getCurrentlyEffectiveTermsByType_ShouldReturnCurrentTerms() throws Exception {
        // Given: 미래 시행일의 약관 추가
        Terms futureTerms = Terms.create(
                Title.of("서비스 이용약관 (미래)"),
                Content.of("서비스 이용약관 내용입니다. (미래)"),
                TermsType.SERVICE,
                Version.of("1.1.0"),
                EffectiveDate.of(LocalDate.now().plusDays(10)),
                Required.of(false)
        );
        termsRepository.save(futureTerms);

        // When
        ResultActions result = mockMvc.perform(get("/api/terms/current")
                .param("type", "SERVICE")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTerms.getId()))
                .andExpect(jsonPath("$.title.value").value(testTerms.getTitle().getValue()))
                .andExpect(jsonPath("$.version.value").value("1.0.0"));
    }

    @Test
    @DisplayName("향후 시행 예정 약관 조회 통합 테스트")
    void getUpcomingTerms_ShouldReturnUpcomingTermsList() throws Exception {
        // Given: 미래 시행일의 약관 추가
        Terms upcomingTerms = Terms.create(
                Title.of("서비스 이용약관 (미래)"),
                Content.of("서비스 이용약관 내용입니다. (미래)"),
                TermsType.SERVICE,
                Version.of("1.1.0"),
                EffectiveDate.of(LocalDate.now().plusDays(10)),
                Required.of(false)
        );
        upcomingTerms = termsRepository.save(upcomingTerms);

        // When
        ResultActions result = mockMvc.perform(get("/api/terms/upcoming")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(upcomingTerms.getId()))
                .andExpect(jsonPath("$[0].title.value").value(upcomingTerms.getTitle().getValue()))
                .andExpect(jsonPath("$[0].effectiveDate.value").value(upcomingTerms.getEffectiveDate().getValue().toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자용 약관 생성 통합 테스트")
    void createTerms_ShouldCreateAndReturnTerms() throws Exception {
        // Given
        CreateTermsRequest createRequest =
                new CreateTermsRequest(
                        Title.of("새로운 약관"), Content.of("새로운 약관 내용"), TermsType.MARKETING, Version.of("1.0.0"), EffectiveDate.of(LocalDate.now()), Required.of(false));

        // When
        ResultActions result = mockMvc.perform(post("/api/admin/terms")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)));

        // Then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title.value").value("새로운 약관"))
                .andExpect(jsonPath("$.content.value").value("새로운 약관 내용"))
                .andExpect(jsonPath("$.type").value("MARKETING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자용 약관 내용 업데이트 통합 테스트")
    void updateTermsContent_ShouldUpdateAndReturnTerms() throws Exception {
        // Given
        UpdateTermsContentRequest updateRequest = new UpdateTermsContentRequest(Content.of("업데이트된 약관 내용"));

        // When
        ResultActions result = mockMvc.perform(put("/api/admin/terms/{id}/content", testTerms.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTerms.getId()))
                .andExpect(jsonPath("$.content.value").value("업데이트된 약관 내용"));
    }
}