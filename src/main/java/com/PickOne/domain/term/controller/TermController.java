package com.PickOne.domain.term.controller;

import com.PickOne.domain.term.dto.TermRequestDto;
import com.PickOne.domain.term.dto.TermResponseDto;
import com.PickOne.domain.term.model.entity.TermEntity;
import com.PickOne.domain.term.service.TermService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
@Tag(name = "Term API", description = "약관 등록, 조회, 삭제 API")
public class TermController {

    private final TermService termService;

    @Operation(summary = "약관 등록", description = "새로운 약관을 등록합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<TermResponseDto>> create(@RequestBody @Valid TermRequestDto request) {
        TermEntity saved = termService.create(request.toEntity());
        return BaseResponse.success(SuccessCode.CREATED, TermResponseDto.fromEntity(saved));
    }

    @Operation(summary = "단일 약관 조회", description = "ID로 약관을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TermResponseDto>> getById(@PathVariable Long id) {
        TermEntity term = termService.getById(id);
        return BaseResponse.success(SuccessCode.OK, TermResponseDto.fromEntity(term));
    }

    @Operation(summary = "전체 약관 목록 조회", description = "등록된 모든 약관을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<TermResponseDto>>> getAll() {
        List<TermResponseDto> responses = termService.getAll().stream()
                .map(TermResponseDto::fromEntity)
                .collect(Collectors.toList());
        return BaseResponse.success(SuccessCode.OK, responses);
    }

    @Operation(summary = "약관 삭제", description = "ID를 기준으로 약관을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        termService.delete(id);
        return BaseResponse.success(SuccessCode.DELETED);
    }
}
