package com.PickOne.user.service;

import com.PickOne.user.exception.EmailAlreadyExistsException;
import com.PickOne.user.exception.PasswordMismatchException;
import com.PickOne.user.exception.UserNotFoundException;
import com.PickOne.user.model.domain.User;
import com.PickOne.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private String generateValidPassword() {
        return "StrongP@ss1234!";
    }

    private String generateUniqueEmail() {
        return "user_" + System.currentTimeMillis() + "@example.com";
    }


    @Test
    @DisplayName("사용자 등록 통합 테스트")
    void registerUserIntegrationTest() {
        // given
        String email = generateUniqueEmail();
        String password = generateValidPassword();

        // when
        User registeredUser = userService.register(email, password);

        // then
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getEmailValue()).isEqualTo(email);
        assertThat(registeredUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("중복 이메일 등록 시 예외 발생 테스트")
    void registerDuplicateEmailTest() {
        // given
        String email = generateUniqueEmail();
        String password1 = generateValidPassword();
        String password2 = generateValidPassword();

        // when
        userService.register(email, password1);

        // then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.register(email, password2);
        });
    }

    @Test
    @DisplayName("ID로 사용자 찾기 통합 테스트")
    void findUserByIdIntegrationTest() {
        // given
        String email = generateUniqueEmail();
        String password = generateValidPassword();
        User registeredUser = userService.register(email, password);

        // when
        User foundUser = userService.findById(registeredUser.getId());

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmailValue()).isEqualTo(email);
        assertThat(foundUser.getId()).isEqualTo(registeredUser.getId());
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 찾기 예외 테스트")
    void findNonExistentUserByIdTest() {
        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            userService.findById(9999L);
        });
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 통합 테스트")
    void findUserByEmailIntegrationTest() {
        // given
        String email = "email@example.com";
        String password = generateValidPassword();
        userService.register(email, password);

        // when
        User foundUser = userService.findByEmail(email);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmailValue()).isEqualTo(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자 찾기 예외 테스트")
    void findNonExistentUserByEmailTest() {
        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            userService.findByEmail("nonexistent@example.com");
        });
    }

    @Test
    @DisplayName("모든 사용자 조회 통합 테스트")
    void findAllUsersIntegrationTest() {
        // given
        userService.register("user1@example.com", generateValidPassword());
        userService.register("user2@example.com", generateValidPassword());

        // when
        List<User> users = userService.findAll();

        // then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getEmailValue)
                .containsExactly("user1@example.com", "user2@example.com");
    }

    @Test
    @DisplayName("비밀번호 변경 통합 테스트")
    void changePasswordIntegrationTest() {
        // given
        String email = generateUniqueEmail();
        String initialPassword = generateValidPassword();
        String newPassword = "NewStrongP@ss456!";

        User registeredUser = userService.register(email, initialPassword);
        System.out.println(registeredUser);
        System.out.println(registeredUser.getId());
        System.out.println(registeredUser.getEmail());
        System.out.println(registeredUser.getEmailValue());

        // when
        User updatedUser = userService.changePassword(
                registeredUser.getId(),
                initialPassword,
                newPassword
        );

        // then
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmailValue()).isEqualTo(email);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 비밀번호 변경 시 예외 발생 테스트")
    void changePasswordWithWrongCurrentPasswordTest() {
        // given
        String email = "passwordchange@example.com";
        String initialPassword = generateValidPassword();
        String newPassword = "NewStrongP@ssw0rd456!";
        User registeredUser = userService.register(email, initialPassword);

        // when & then
        assertThrows(PasswordMismatchException.class, () -> {
            userService.changePassword(
                    registeredUser.getId(),
                    "WrongPassword3!C",
                    newPassword
            );
        });
    }

    @Test
    @DisplayName("사용자 삭제 통합 테스트")
    void deleteUserIntegrationTest() {
        // given
        String email = "delete@example.com";
        String password = generateValidPassword();
        User registeredUser = userService.register(email, password);

        // when
        userService.delete(registeredUser.getId());

        // then
        assertThrows(UserNotFoundException.class, () -> {
            userService.findById(registeredUser.getId());
        });
    }
}