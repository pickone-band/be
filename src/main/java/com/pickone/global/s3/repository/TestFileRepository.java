package com.pickone.global.s3.repository;


import com.pickone.global.s3.entity.TestFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestFileRepository extends JpaRepository<TestFileEntity, Long> {
    // 기본 CRUD 메서드 제공
}

