package com.pickone.domain.term.controller;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.service.TermCommandService;
import com.pickone.domain.term.service.TermQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
@Tag(name = "Term", description = "이용약관 및 정책 관련 API")
public class TermController {

  private final TermCommandService commandService;
  private final TermQueryService queryService;

  @Operation(summary = "약관 생성", description = "관리자가 새로운 약관을 생성합니다.")
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public TermResponseDto create(
      @RequestBody TermRequestDto dto) {
    return commandService.createTerm(dto);
  }

  @Operation(summary = "약관 수정", description = "관리자가 기존 약관을 수정합니다.")
  @PatchMapping("/{termId}")
  @PreAuthorize("hasRole('ADMIN')")
  public void update(
      @Parameter(description = "약관 ID") @PathVariable Long termId,
      @RequestBody TermRequestDto dto) {
    commandService.updateTerm(termId, dto);
  }

  @Operation(summary = "약관 삭제", description = "관리자가 특정 약관을 삭제합니다.")
  @DeleteMapping("/{termId}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(
      @Parameter(description = "약관 ID") @PathVariable Long termId) {
    commandService.deleteTerm(termId);
  }

  @Operation(summary = "약관 단건 조회", description = "특정 ID의 약관 상세 정보를 조회합니다.")
  @GetMapping("/{termId}")
  public TermResponseDto get(
      @Parameter(description = "약관 ID") @PathVariable Long termId) {
    return queryService.getTerm(termId);
  }

  @Operation(summary = "최신 약관 목록 조회", description = "최신 버전의 모든 약관 목록을 조회합니다.")
  @GetMapping("/latest")
  public List<TermResponseDto> latest() {
    return queryService.getLatestTerms();
  }
}
