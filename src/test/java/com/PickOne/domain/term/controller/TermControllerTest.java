package com.PickOne.domain.term.controller;

import com.PickOne.domain.notification.controller.NotificationController;
import com.PickOne.domain.term.dto.TermRequestDto;
import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.service.TermService;
import com.PickOne.global.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(
        controllers = TermController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class)
)
class TermControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private TermService termService;
    @Autowired private ObjectMapper objectMapper;

    private final Term mockTerm = new Term(
            1L, "제목", "내용", "v1.0", true,
            LocalDateTime.of(2025, 1, 1, 0, 0)
    );

    @Test
    void 약관_등록_API() throws Exception {
        TermRequestDto request = new TermRequestDto(
                "제목", "내용", "v1.0", true,
                LocalDateTime.of(2025, 1, 1, 0, 0)
        );

        Mockito.when(termService.create(any())).thenReturn(mockTerm);

        mockMvc.perform(post("/api/terms")
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.version").value("v1.0"));
    }

    @Test
    void 약관_단건_조회_API() throws Exception {
        Mockito.when(termService.getById(1L)).thenReturn(mockTerm);

        mockMvc.perform(get("/api/terms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.required").value(true));
    }

    @Test
    void 약관_전체_조회_API() throws Exception {
        Mockito.when(termService.getAll()).thenReturn(List.of(mockTerm));

        mockMvc.perform(get("/api/terms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void 약관_삭제_API() throws Exception {
        mockMvc.perform(delete("/api/terms/1")
                        .with(csrf())) // CSRF 토큰 추가
                .andExpect(status().isNoContent());

        Mockito.verify(termService).delete(1L);
    }
}
