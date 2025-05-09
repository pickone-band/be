package com.PickOne.global.oauth2.repository;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.entity.UserConnectionEntity;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserConnectionRepository 테스트 클래스
 * JPA Auditing을 활용하여 BaseEntity의 필드가 자동으로 설정되도록 함
 */
@DataJpaTest
@ActiveProfiles("test") // test 프로필 활성화 - AuditConfig의 testAuditorProvider가 사용됨
@Import(com.PickOne.global.common.config.AuditConfig.class) // AuditConfig 임포트
class UserConnectionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private UserEntity userEntity;
    private UserConnectionEntity connectionEntity;

    @BeforeEach
    void setUp() {
        // User 도메인 객체 생성
        User user = User.of(
                null,
                Email.of("test@example.com"),
                Password.ofEncoded("encoded_password")
        );

        // 도메인 객체로부터 엔티티 생성
        userEntity = UserEntity.from(user);

        // JPA를 통해 엔티티 저장
        // AuditingEntityListener가 자동으로 created_at 및 기타 감사 필드를 설정함
        userEntity = entityManager.persist(userEntity);

        // 사용자 연결 엔티티 생성 및 저장
        connectionEntity = UserConnectionEntity.create(
                userEntity,
                "12345",
                OAuth2Provider.GOOGLE,
                "test@example.com",
                "Test User"
        );
        connectionEntity = entityManager.persist(connectionEntity);

        entityManager.flush();
    }

    @Test
    @DisplayName("제공자와 제공자 ID로 사용자 연결 조회")
    void findByProviderAndProviderId() {
        // when
        Optional<UserConnectionEntity> found = userConnectionRepository.findByProviderAndProviderId(
                OAuth2Provider.GOOGLE, "12345"
        );

        // then
        assertTrue(found.isPresent());
        assertEquals(connectionEntity.getId(), found.get().getId());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("사용자로 사용자 연결 목록 조회")
    void findByUser() {
        // when
        List<UserConnectionEntity> connections = userConnectionRepository.findByUser(userEntity);

        // then
        assertFalse(connections.isEmpty());
        assertEquals(1, connections.size());
        assertEquals(connectionEntity.getId(), connections.get(0).getId());
    }

    @Test
    @DisplayName("사용자 ID로 사용자 연결 목록 조회")
    void findByUserId() {
        // when
        List<UserConnectionEntity> connections = userConnectionRepository.findByUserId(userEntity.getId());

        // then
        assertFalse(connections.isEmpty());
        assertEquals(1, connections.size());
        assertEquals(connectionEntity.getId(), connections.get(0).getId());
    }

    @Test
    @DisplayName("사용자 ID와 제공자로 사용자 연결 조회")
    void findByUserIdAndProvider() {
        // when
        Optional<UserConnectionEntity> found = userConnectionRepository.findByUserIdAndProvider(
                userEntity.getId(), OAuth2Provider.GOOGLE
        );

        // then
        assertTrue(found.isPresent());
        assertEquals(connectionEntity.getId(), found.get().getId());
    }

    @Test
    @DisplayName("제공자와 제공자 ID의 존재 여부 확인")
    void existsByProviderAndProviderId() {
        // when
        boolean exists = userConnectionRepository.existsByProviderAndProviderId(
                OAuth2Provider.GOOGLE, "12345"
        );
        boolean notExists = userConnectionRepository.existsByProviderAndProviderId(
                OAuth2Provider.GOOGLE, "non-existent"
        );

        // then
        assertTrue(exists);
        assertFalse(notExists);
    }
}