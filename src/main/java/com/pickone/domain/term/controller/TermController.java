package com.pickone.domain.term.controller;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.manager.TermManager;
import com.pickone.domain.term.policy.TermPolicy;
import com.pickone.domain.term.service.TermService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Term API", description = "약관 등록, 조회 API")
public class TermController {

  private final TermManager termManager;
  private final TermPolicy termPolicy;
  private final TermService termService;

  @Operation(summary = "약관 등록", description = "새로운 약관을 등록합니다.")
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<BaseResponse<TermResponseDto>> create(@RequestBody @Valid TermRequestDto request) {
    TermEntity saved = termManager.register(
        request.title(),
        request.content(),
        request.version(),
        request.required(),
        request.effectiveDate()
    );
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
    List<TermResponseDto> terms = termPolicy.getAllTerms().stream()
        .map(TermResponseDto::fromEntity)
        .toList();
    return BaseResponse.success(SuccessCode.OK, terms);
  }
}
