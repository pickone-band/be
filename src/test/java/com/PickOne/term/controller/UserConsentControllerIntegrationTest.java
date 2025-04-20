package com.PickOne.term.controller;

import com.PickOne.term.controller.dto.userConsent.MarketingConsentRequest;
import com.PickOne.term.controller.dto.userConsent.ConsentRequest;
import com.PickOne.term.model.domain.*;
import com.PickOne.term.repository.terms.TermsRepository;
import com.PickOne.term.repository.userConsent.UserConsentRepository;
import com.PickOne.term.service.UserConsentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 사용자 약관 동의 컨트롤러 통합 테스트
 * 참고: 시큐리티 설정이 필요하지만 아직 설정되지 않은 상황을 고려하여
 * 필요한 곳에 @WithMockUser 어노테이션을 추가하고 시큐리티 관련 설정은 주석으로 표시
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserConsentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private UserConsentRepository userConsentRepository;

    @Autowired
    private UserConsentService userConsentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Terms testTerms;
    private Terms marketingTerms;
    private UserConsent testUserConsent;
    private final Long TEST_USER_ID = 100L;

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
                Required.of(true)
        );
        // DB에 저장
        testTerms = termsRepository.save(testTerms);

        // 마케팅 약관 생성
        marketingTerms = Terms.create(
                Title.of("마케팅 정보 수신 동의"),
                Content.of("마케팅 정보 수신 동의 내용입니다."),
                TermsType.MARKETING,
                Version.of("1.0.0"),
                EffectiveDate.of(today),
                Required.of(false)
        );
        // DB에 저장
        marketingTerms = termsRepository.save(marketingTerms);

        // 테스트용 사용자 동의 데이터 생성
        testUserConsent = UserConsent.of(
                TEST_USER_ID,
                testTerms.getId(),
                today,true
        );
        // DB에 저장
        testUserConsent = userConsentRepository.save(testUserConsent);
    }

    @AfterEach
    void tearDown() {
        userConsentRepository.deleteAll();
        termsRepository.deleteAll();
    }

    /**
     * 시큐리티 설정이 없는 상황에서 테스트를 위한 모킹 메서드
     * 실제 시큐리티 설정이 완료되면 이 메서드는 필요 없음
     */
    private void mockSecurityForUser(Long userId) {
        // 여기에 시큐리티 관련 모킹 로직이 들어갈 수 있음
        // 현재는 @WithMockUser를 사용하고, securityContext를 수동으로 조작하는 방식으로 대체
    }

    @Test
    @WithMockUser(username = "user100")
    @DisplayName("약관 동의 생성 통합 테스트")
    void createOrUpdateConsent_ShouldCreateAndReturnConsent() throws Exception {
        // Given
        mockSecurityForUser(TEST_USER_ID);
        ConsentRequest request = new ConsentRequest(marketingTerms.getId(), true);

        // When
        ResultActions result = mockMvc.perform(post("/api/user-consent")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.termsId").value(marketingTerms.getId()))
                .andExpect(jsonPath("$.consented").value(true));
    }

    @Test
    @WithMockUser(username = "user100")
    @DisplayName("약관 동의 여부 확인 통합 테스트")
    void hasUserConsented_ShouldReturnConsentStatus() throws Exception {
        // Given
        mockSecurityForUser(TEST_USER_ID);

        // When
        ResultActions result = mockMvc.perform(get("/api/user-consent/check/{termsId}", testTerms.getId()));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consented").value(true));
    }

    @Test
    @WithMockUser(username = "user100")
    @DisplayName("약관 유형별 동의 여부 확인 통합 테스트")
    void hasUserConsentedToType_ShouldReturnConsentStatus() throws Exception {
        // Given
        mockSecurityForUser(TEST_USER_ID);

        // When
        ResultActions result = mockMvc.perform(get("/api/user-consent/check/type")
                .param("type", "SERVICE"));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consented").value(true));
    }

    @Test
    @WithMockUser(username = "user100")
    @DisplayName("필수 약관 전체 동의 여부 확인 통합 테스트")
    void hasUserConsentedToAllRequiredTerms_ShouldReturnConsentStatus() throws Exception {
        // Given
        mockSecurityForUser(TEST_USER_ID);

        // When
        ResultActions result = mockMvc.perform(get("/api/user-consent/check/required"));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consented").value(true));
    }

    @Test
    @WithMockUser(username = "user100")
    @DisplayName("사용자의 모든 동의 정보 조회 통합 테스트")
    void getAllUserConsents_ShouldReturnConsentList() throws Exception {
        // Given
        mockSecurityForUser(TEST_USER_ID);

        // When
        ResultActions result = mockMvc.perform(get("/api/user-consent"));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].termsId").value(testTerms.getId()))
                .andExpect(jsonPath("$[0].consented").value(true));
    }

    @Test
    @WithMockUser(username = "user100")
    @DisplayName("마케팅 수신 동의 업데이트 통합 테스트")
    void updateMarketingConsent_ShouldUpdateAndReturnConsent() throws Exception {
        // Given
        mockSecurityForUser(TEST_USER_ID);
        MarketingConsentRequest request = new MarketingConsentRequest(true);

        // When
        ResultActions result = mockMvc.perform(post("/api/user-consent/marketing")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consented").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자의 사용자 동의 정보 조회 통합 테스트")
    void getAllUserConsentsForAdmin_ShouldReturnConsentList() throws Exception {
        // When
        ResultActions result = mockMvc.perform(get("/api/admin/user-consent/{userId}", TEST_USER_ID));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$[0].termsId").value(testTerms.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("관리자의 특정 약관 동의 사용자 목록 조회 통합 테스트")
    void getUsersConsentedToTerms_ShouldReturnUserIdList() throws Exception {
        // When
        ResultActions result = mockMvc.perform(get("/api/admin/user-consent/users/{termsId}", testTerms.getId()));

        // Then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(TEST_USER_ID));
    }
}