package com.pickone.domain.userPerformance.controller;

import com.pickone.domain.userPerformance.dto.PerformanceRecordRequest;
import com.pickone.domain.userPerformance.dto.PerformanceRecordResponse;
import com.pickone.domain.userPerformance.service.PerformanceRecordService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공연기록", description = "공연기록 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/performances")
public class PerformanceRecordController {

    private final PerformanceRecordService performanceRecordService;

    // 공연 등록
    @Operation(summary = "공연기록 업로드", description = "공연기록을 업로드 합니다.")
    @PostMapping()
    public ResponseEntity<BaseResponse<Void>> createPerformance(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody PerformanceRecordRequest request
    ) {
        performanceRecordService.save(userId, request);
        return BaseResponse.success();
    }

    // 특정 유저의 공연 목록 조회
    @Operation(summary = "공연기록 조회", description = "사용자의 모든 공연기록을 조회합니다")
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<PerformanceRecordResponse>>> getPerformancesByUser(
            @PathVariable Long userId
    ) {
        List<PerformanceRecordResponse> responses = performanceRecordService.findByUser(userId);
        return BaseResponse.success(responses);
    }
}
