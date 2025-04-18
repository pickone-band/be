package com.PickOne.user.controller;

import com.PickOne.user.controller.dto.PasswordChangeRequest;
import com.PickOne.user.controller.dto.UserRegistrationRequest;
import com.PickOne.user.controller.dto.UserResponse;
import com.PickOne.user.model.entity.UserEntity;
import com.PickOne.user.repository.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private UserResponse registeredUser;

    @BeforeEach
    void setup() throws Exception {
        userJpaRepository.deleteAll();

        // 테스트용 사용자 등록
        UserRegistrationRequest request = new UserRegistrationRequest(
                "SetUpTest@example.com",
                "Password1!"
        );

        MvcResult registerResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        registeredUser = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                UserResponse.class
        );
    }

    @AfterEach
    void cleanup() {
        userJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 등록 성공 테스트")
    void registerUserSuccessTest() throws Exception {
        // given
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test@example.com",
                "Password1!"
        );

        // when - 사용자 등록
        MvcResult registerResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        // then - DB에서 직접 확인
        assertThat(userJpaRepository.findById(userResponse.id())).isPresent();
    }

    @Test
    @DisplayName("중복 이메일 등록 실패 테스트")
    void duplicateEmailRegistrationFailTest() throws Exception {
        // given - 첫 번째 사용자 등록
        UserRegistrationRequest request = new UserRegistrationRequest(
                "duplicate@example.com",
                "Password1!"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // when - 같은 이메일로 두 번째 사용자 등록 시도
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("ID로 사용자 조회 테스트")
    void findUserByIdTest() throws Exception {
        mockMvc.perform(get("/api/users/{id}", registeredUser.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(registeredUser.id().intValue())))
                .andExpect(jsonPath("$.email", is("SetUpTest@example.com")));
    }

    @Test
    @DisplayName("이메일로 사용자 조회 테스트")
    void findUserByEmailTest() throws Exception {
        mockMvc.perform(get("/api/users/email/{email}", "SetUpTest@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(registeredUser.id().intValue())))
                .andExpect(jsonPath("$.email", is("SetUpTest@example.com")));
    }

    @Test
    @DisplayName("모든 사용자 조회 테스트")
    void findAllUsersTest() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(registeredUser.id().intValue())))
                .andExpect(jsonPath("$[0].email", is("SetUpTest@example.com")));
    }

    @Test
    @DisplayName("올바른 비밀번호로 변경 성공 테스트")
    void changePasswordSuccessTest() throws Exception {
        // given
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(
                "Password1!",
                "NewPassword1!"
        );

        // when - 비밀번호 변경
        mockMvc.perform(patch("/api/users/{id}/password", registeredUser.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isOk());

        // then - 엔티티 존재 확인
        UserEntity userEntity = userJpaRepository.findById(registeredUser.id()).orElseThrow();
        assertThat(userEntity.getEmail()).isEqualTo("SetUpTest@example.com");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 변경 실패 테스트")
    void changePasswordWithWrongCurrentPasswordFailTest() throws Exception {
        // given
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(
                "WrongPassword1!",
                "NewPassword1!"
        );

        // when & then - 잘못된 현재 비밀번호로 변경 시도
        mockMvc.perform(patch("/api/users/{id}/password", registeredUser.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordChangeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("사용자 삭제 성공 테스트")
    void deleteUserSuccessTest() throws Exception {
        // given - 사용자 등록
        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "delete-test@example.com",
                "Password1!"
        );

        MvcResult registerResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse userResponse = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(),
                UserResponse.class
        );
        assertThat(userResponse.id()).isNotNull();

        // when - 사용자 삭제
        mockMvc.perform(delete("/api/users/{id}", userResponse.id()))
                .andExpect(status().isNoContent());

        // then - 삭제 확인
        assertThat(userJpaRepository.findById(userResponse.id())).isEmpty();
    }
}
