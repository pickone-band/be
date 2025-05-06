package com.PickOne.common.entity;

import com.PickOne.global.common.entity.BaseEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Import(BaseEntityTest.TestAuditingConfig.class)
class BaseEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    // BaseEntity를 테스트하기 위한 구체 클래스
    @Entity
    @Table(name = "test_entities")
    public static class TestEntity extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public TestEntity() {
            this.name = "Test";
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // 테스트를 위한 JPA Auditing 설정
    @EnableJpaAuditing
    static class TestAuditingConfig {
        // 필요한 경우 AuditorAware 빈을 여기에 추가
    }

    @Test
    @DisplayName("새 엔티티 저장 시 생성 시간과 수정 시간 자동 설정")
    void createdAndUpdatedTimestampsAreSet() {
        // given
        TestEntity entity = new TestEntity();

        // when
        TestEntity savedEntity = entityManager.persistAndFlush(entity);

        // then
        assertNotNull(savedEntity.getCreatedAt());
        assertNotNull(savedEntity.getUpdatedAt());

        // 생성 시간과 수정 시간이 유사한 시점에 설정되었는지 확인
        assertTrue(savedEntity.getCreatedAt().isEqual(savedEntity.getUpdatedAt()) ||
                savedEntity.getCreatedAt().isBefore(savedEntity.getUpdatedAt()));
    }

    @Test
    @DisplayName("엔티티 업데이트 시 수정 시간만 업데이트")
    void updatedTimestampIsModifiedOnUpdate() {
        // given
        TestEntity entity = new TestEntity();
        TestEntity savedEntity = entityManager.persistAndFlush(entity);
        LocalDateTime initialCreatedAt = savedEntity.getCreatedAt();
        LocalDateTime initialUpdatedAt = savedEntity.getUpdatedAt();

        // 테스트를 위해 약간의 시간 지연
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("테스트 중 인터럽트 발생");
        }

        // when
        savedEntity.setName("Updated Test");
        entityManager.persistAndFlush(savedEntity);

        // 영속성 컨텍스트 초기화하여 엔티티를 다시 로드
        entityManager.clear();
        TestEntity updatedEntity = entityManager.find(TestEntity.class, savedEntity.getId());

        // then
        // 초 단위로 잘라낸 후 비교 (나노초 차이 무시)
        assertEquals(initialCreatedAt.truncatedTo(java.time.temporal.ChronoUnit.MILLIS),
                updatedEntity.getCreatedAt().truncatedTo(java.time.temporal.ChronoUnit.MILLIS),
                "생성 시간은 변경되지 않아야 함");

        // 대안적 접근: 시간이 변경되었는지 여부 확인
        assertTrue(updatedEntity.getUpdatedAt().isAfter(initialUpdatedAt),
                "수정 시간은 원래 시간보다 이후여야 함");
    }

//    @Test
//    @DisplayName("상태 변경 메서드가 엔티티 상태를 올바르게 변경")
//    void statusChangeMethodsWorkCorrectly() {
//        // given
//        TestEntity entity = new TestEntity();
//
//        // when & then
//        // 기본 상태는 ACTIVE
//        assertEquals(EntityStatus.ACTIVE, entity.getStatus());
//        assertTrue(entity.isActive());
//
//        // INACTIVE로 변경
//        entity.inactivate();
//        assertEquals(EntityStatus.INACTIVE, entity.getStatus());
//        assertTrue(entity.isInactive());
//        assertFalse(entity.isActive());
//
//        // ACTIVE로 다시 변경
//        entity.activate();
//        assertEquals(EntityStatus.ACTIVE, entity.getStatus());
//        assertTrue(entity.isActive());
//
//        // DELETED로 변경
//        entity.markDeleted();
//        assertEquals(EntityStatus.DELETED, entity.getStatus());
//        assertTrue(entity.isDeleted());
//
//        // 변경사항 저장 및 확인
//        TestEntity savedEntity = entityManager.persistAndFlush(entity);
//        assertEquals(EntityStatus.DELETED, savedEntity.getStatus());
//    }
}