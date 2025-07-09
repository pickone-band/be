package com.pickone.domain.term.controller;

import com.pickone.domain.term.dto.TermRequest;
import com.pickone.domain.term.dto.TermResponse;
import com.pickone.domain.term.service.TermCommandService;
import com.pickone.domain.term.service.TermQueryService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
  public ResponseEntity<BaseResponse<TermResponse>> create(@RequestBody TermRequest dto) {
    log.info("[TermController] 약관 생성 요청: title={}, version={}", dto.title(), dto.version());
    TermResponse response = commandService.createTerm(dto);
    return BaseResponse.success(response);
  }

  @Operation(summary = "약관 수정", description = "관리자가 기존 약관을 수정합니다.")
  @PatchMapping("/{termId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> update(
      @Parameter(description = "약관 ID") @PathVariable Long termId,
      @RequestBody TermRequest dto
  ) {
    log.info("[TermController] 약관 수정 요청: id={}", termId);
    commandService.updateTerm(termId, dto);
    return BaseResponse.success();
  }

  @Operation(summary = "약관 삭제", description = "관리자가 특정 약관을 삭제합니다.")
  @DeleteMapping("/{termId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BaseResponse<Void>> delete(@Parameter(description = "약관 ID") @PathVariable Long termId) {
    log.info("[TermController] 약관 삭제 요청: id={}", termId);
    commandService.deleteTerm(termId);
    return BaseResponse.success();
  }

  @Operation(summary = "약관 단건 조회", description = "특정 ID의 약관 상세 정보를 조회합니다.")
  @GetMapping("/{termId}")
  public ResponseEntity<BaseResponse<TermResponse>> get(@Parameter(description = "약관 ID") @PathVariable Long termId) {
    log.info("[TermController] 약관 단건 조회 요청: id={}", termId);
    return BaseResponse.success(queryService.getTerm(termId));
  }

  @Operation(summary = "최신 약관 목록 조회", description = "최신 버전의 모든 약관 목록을 조회합니다.")
  @GetMapping("/latest")
  public ResponseEntity<BaseResponse<List<TermResponse>>> latest() {
    log.info("[TermController] 최신 약관 목록 조회 요청");
    return BaseResponse.success(queryService.getLatestTerms());
  }
}
