package com.PickOne.global.oauth2.controller;

import com.PickOne.domain.user.model.domain.*;
import com.PickOne.global.oauth2.service.CustomOAuth2UserService;
import com.PickOne.global.security.filter.JwtAuthenticationFilter;
import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "user", roles = "USER")
@WebMvcTest(
        controllers = OAuth2Controller.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class OAuth2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    @DisplayName("지원되는 OAuth2 provider에 대해 로그인 URL을 반환한다")
    void getOAuth2LoginUrl_validProvider_returnsUrl() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/url/google"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorizationUrl").value("/oauth2/authorize/google"));
    }

    @Test
    @DisplayName("지원하지 않는 provider일 경우 400 에러 반환")
    void getOAuth2LoginUrl_invalidProvider_returnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/url/unknown"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 리다이렉트 URL 반환")
    void redirectToOAuth2Login_validProvider_returnsRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/login/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/oauth2/authorize/google"));
    }

    @Test
    @DisplayName("지원하지 않는 provider일 경우 로그인 에러 리다이렉트")
    void redirectToOAuth2Login_invalidProvider_returnsErrorRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/login/unknown"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=unsupported_provider"));
    }

    @Test
    @DisplayName("인증된 사용자는 토큰을 발급받는다")
    @WithMockUser
    void getCurrentUser_authenticated_returnsToken() throws Exception {
        UserPrincipal principal = UserPrincipal.from(
                new User(
                        1L,
                        Email.of("test@example.com"),
                        Password.ofEncoded("encoded"),
                        new Nickname("nickname"),
                        Gender.MALE,  // 성별 명시
                        LocalDate.of(1990, 1, 1),  // 생일 명시
                        new ProfileImage("https://example.com/image.png"),
                        true,    // isPublic
                        true,    // isVerified
                        false,   // isOauth
                        Role.USER,
                        List.of(new Instrument("ELECTRIC_GUITAR")),
                        List.of(new Genre("ROCK"))
                ));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(jwtService.generateAccessToken(Mockito.any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(Mockito.any())).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600000L);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/user")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600000));
    }

    @Test
    @DisplayName("비인증 사용자는 401 반환")
    void getCurrentUser_unauthenticated_returnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/oauth2/user"))
                .andExpect(status().isUnauthorized());
    }
}
