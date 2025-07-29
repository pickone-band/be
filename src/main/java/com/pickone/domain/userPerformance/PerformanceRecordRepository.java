package com.pickone.domain.userPerformance;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRecordRepository extends JpaRepository<PerformanceRecord,Long> {
    List<PerformanceRecord> findByUserId(Long userId);
}
