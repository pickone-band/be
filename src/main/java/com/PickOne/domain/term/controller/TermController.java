package com.PickOne.domain.term.controller;

import com.PickOne.domain.term.dto.TermRequestDto;
import com.PickOne.domain.term.dto.TermResponseDto;
import com.PickOne.domain.term.mapper.TermMapper;
import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.service.TermService;
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
    public ResponseEntity<TermResponseDto> create(@RequestBody @Valid TermRequestDto request) {
        Term saved = termService.create(request.toDomain(null));
        return ResponseEntity.ok(TermMapper.toResponse(saved));
    }

    @Operation(summary = "단일 약관 조회", description = "ID로 약관을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<TermResponseDto> getById(@PathVariable Long id) {
        Term term = termService.getById(id);
        return ResponseEntity.ok(TermMapper.toResponse(term));
    }

    @Operation(summary = "전체 약관 목록 조회", description = "등록된 모든 약관을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<TermResponseDto>> getAll() {
        List<TermResponseDto> responses = termService.getAll().stream()
                .map(TermMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "약관 삭제", description = "ID를 기준으로 약관을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        termService.delete(id);
        return ResponseEntity.noContent().build();
    }
}