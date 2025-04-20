package com.PickOne.user.repository;

import com.PickOne.common.config.AuditConfig;
import com.PickOne.common.config.SecurityConfig;
import com.PickOne.user.model.domain.Email;
import com.PickOne.user.model.domain.Password;
import com.PickOne.user.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.query.sqm.tree.SqmNode.log;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({AuditConfig.class, SecurityConfig.class})
class JpaUserRepositoryIntegrationTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setup() {
        jpaUserRepository = new JpaUserRepository(userJpaRepository);
    }

    private User createTestUser(String emailValue, String passwordValue) {
        Email email = Email.of(emailValue);
        // 수정된 부분: 비밀번호 생성 규칙에 맞는 비밀번호로 변경
        Password password = Password.of(passwordValue + "A1!");
        return User.create(email, password);
    }

    @Test
    @DisplayName("사용자 저장 테스트")
    void testSaveUser() {
        // given
        User user = createTestUser("test@example.com", "Password");

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmailValue()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("ID로 사용자 찾기 테스트")
    void testFindById() {
        // given
        User user = createTestUser("find@example.com", "Password");
        User savedUser = jpaUserRepository.save(user);

        // when
        Optional<User> foundUser = jpaUserRepository.findById(savedUser.getId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmailValue()).isEqualTo("find@example.com");
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 테스트")
    void testFindByEmail() {
        // given
        User user = createTestUser("email@example.com", "Password");
        jpaUserRepository.save(user);

        // when
        Optional<User> foundUser = jpaUserRepository.findByEmail("email@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmailValue()).isEqualTo("email@example.com");
    }

    @Test
    @DisplayName("모든 사용자 조회 테스트")
    void testFindAll() {
        // given
        User user1 = createTestUser("user1@example.com", "Password1");
        User user2 = createTestUser("user2@example.com", "Password2");
        jpaUserRepository.save(user1);
        jpaUserRepository.save(user2);

        // when
        List<User> users = jpaUserRepository.findAll();

        // then
        assertThat(users).hasSize(2);
        assertThat(users)
                .extracting(User::getEmailValue)
                .containsExactly("user1@example.com", "user2@example.com");
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    void testDeleteUser() {
        // given
        User user = createTestUser("delete@example.com", "Password");
        User savedUser = jpaUserRepository.save(user);

        // when
        jpaUserRepository.delete(savedUser);

        // then
        Optional<User> deletedUser = jpaUserRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회 테스트")
    void testFindByNonExistentEmail() {
        // when
        Optional<User> foundUser = jpaUserRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("중복 이메일 저장 시 예외 발생 테스트")
    void testSaveDuplicateEmail() {
        // given
        User user1 = createTestUser("duplicate@example.com", "Password");
        jpaUserRepository.save(user1);

        // when & then
        User user2 = createTestUser("duplicate@example.com", "AnotherPassword");
        assertThrows(Exception.class, () -> {
            jpaUserRepository.save(user2);
        });
    }
}