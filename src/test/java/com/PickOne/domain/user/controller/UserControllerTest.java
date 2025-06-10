package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.UserUpdateRequest;
import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;

    private User mockUser(Long id) {
        return new User(
                id,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded123"),
                new Nickname("tester"),
                new ProfileImage("https://cdn.com/image.jpg"),
                true,
                true,
                false,
                Role.USER,
                List.of(new Instrument("Guitar")),
                List.of(new Genre("Rock"))
        );
    }

    @Test
    @DisplayName("회원 정보 조회 - 성공")
    void getUserById_success() throws Exception {
        Long userId = 1L;
        User user = mockUser(userId);
        when(userService.findById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("tester"))
                .andExpect(jsonPath("$.profileImageUrl").value("https://cdn.com/image.jpg"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateUser_success() throws Exception {
        Long userId = 1L;
        UserUpdateRequest request = new UserUpdateRequest(
                "newNick", "https://new.img", false,
                List.of("Drums"), List.of("Jazz")
        );

        User currentUser = mockUser(userId);
        User updatedUser = request.toUpdatedDomain(currentUser);

        when(userService.findById(userId)).thenReturn(currentUser);
        doNothing().when(userService).updateUser(eq(userId), any(User.class));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("회원 탈퇴 - 성공")
    void deleteUser_success() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("악기명으로 사용자 검색 - 성공")
    void getUsersByInstrument_success() throws Exception {
        String instrument = "Guitar";
        User user = mockUser(1L);
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.findUsersByInstrument(eq(instrument), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        mockMvc.perform(get("/api/users/search/instrument")
                        .param("instrument", instrument))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nickname").value(user.getNickname().getValue()));
    }

    @Test
    @DisplayName("장르명으로 사용자 검색 - 성공")
    void getUsersByGenre_success() throws Exception {
        String genre = "Jazz";
        User user = mockUser(2L);
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.findUsersByGenre(eq(genre), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        mockMvc.perform(get("/api/users/search/genre")
                        .param("genre", genre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nickname").value(user.getNickname().getValue()));
    }

    @Test
    @DisplayName("키워드로 사용자 검색 - 성공")
    void searchUsers_success() throws Exception {
        String keyword = "test";
        boolean onlyPublic = true;
        User user = mockUser(3L);
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.searchUsers(eq(keyword), eq(onlyPublic), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        mockMvc.perform(get("/api/users/search")
                        .param("keyword", keyword)
                        .param("onlyPublic", String.valueOf(onlyPublic)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value(user.getEmail().getValue()));
    }
}
