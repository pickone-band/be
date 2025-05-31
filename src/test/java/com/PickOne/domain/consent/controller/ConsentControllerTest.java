package com.PickOne.domain.consent.controller;

import com.PickOne.domain.consent.dto.ConsentRequestDto;
import com.PickOne.domain.consent.model.domain.Consent;
import com.PickOne.domain.consent.service.ConsentService;
import com.PickOne.global.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(
        controllers = ConsentController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class ConsentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsentService consentService;

    @Test
    @DisplayName("POST /api/consents/{userId} - 동의 저장")
    void saveConsent_success() throws Exception {
        ConsentRequestDto request = new ConsentRequestDto(100L, true);
        Consent saved = new Consent(1L, 100L, true, LocalDateTime.now());

        when(consentService.saveConsent(any())).thenReturn(saved);

        mockMvc.perform(post("/api/consents/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.termsId").value(100L))
                .andExpect(jsonPath("$.consented").value(true));
    }

    @Test
    @DisplayName("GET /api/consents/{userId} - 사용자 동의 목록 조회")
    void getUserConsents_success() throws Exception {
        List<Consent> consents = List.of(
                new Consent(1L, 100L, true, LocalDateTime.now()),
                new Consent(1L, 101L, false, LocalDateTime.now())
        );
        when(consentService.getUserConsents(1L)).thenReturn(consents);

        mockMvc.perform(get("/api/consents/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/consents/{userId}/check/{termsId} - 동의 여부 확인")
    void hasConsented_success() throws Exception {
        when(consentService.hasConsented(1L, 100L)).thenReturn(true);

        mockMvc.perform(get("/api/consents/{userId}/check/{termsId}", 1L, 100L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("DELETE /api/consents/{userId}/{termsId} - 동의 삭제")
    void deleteConsent_success() throws Exception {
        doNothing().when(consentService).deleteConsent(1L, 100L);

        mockMvc.perform(delete("/api/consents/{userId}/{termsId}", 1L, 100L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
