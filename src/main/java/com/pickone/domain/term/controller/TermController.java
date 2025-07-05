package com.pickone.domain.term.controller;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.service.TermCommandService;
import com.pickone.domain.term.service.TermQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermController {

  private final TermCommandService commandService;
  private final TermQueryService queryService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public TermResponseDto create(@RequestBody TermRequestDto dto) {
    return commandService.createTerm(dto);
  }

  @PatchMapping("/{termId}")
  @PreAuthorize("hasRole('ADMIN')")
  public void update(@PathVariable Long termId, @RequestBody TermRequestDto dto) {
    commandService.updateTerm(termId, dto);
  }

  @DeleteMapping("/{termId}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long termId) {
    commandService.deleteTerm(termId);
  }

  @GetMapping("/{termId}")
  public TermResponseDto get(@PathVariable Long termId) {
    return queryService.getTerm(termId);
  }

  @GetMapping("/latest")
  public List<TermResponseDto> latest() {
    return queryService.getLatestTerms();
  }
}
