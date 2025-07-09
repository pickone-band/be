package com.pickone.domain.recruitments.controller;

import com.pickone.domain.recruitments.dto.request.RecruitmentRequestDto;
import com.pickone.domain.recruitments.dto.response.RecruitmentResponseDto;
import com.pickone.domain.recruitments.service.RecruitmentService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.security.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "멤버 모집글 작성 API", description = "멤버 모집글 작성관련 API")
@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @Operation(summary = "멤버 모집글 등록 기능",
            description = "<a href='https://www.notion.so/1ae9366dcb7d812a8485e5b04db6adc6' target='_blank'>👉API 명세서 바로가기</a>")
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> createRecruitment(@AuthenticationPrincipal UserPrincipal user,
                                                                @RequestBody RecruitmentRequestDto requestDto) {

        Long recruitmentId = recruitmentService.registerRecruitment(requestDto, user.getId());
        return BaseResponse.success(recruitmentId);
    }

    @Operation(summary = "멤버 모집글 단건 조회 기능",
            description = "<a href='https://www.notion.so/1bc9366dcb7d80fcbab2e22b33082108' target='_blank'>👉API 명세서 바로가기</a>")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RecruitmentResponseDto>> getRecruitment(@PathVariable Long id) {
        RecruitmentResponseDto recruitment = recruitmentService.getRecruitment(id);
        return BaseResponse.success(recruitment);
    }

    @Operation(summary = "멤버 모집글 전체 조회 기능",
            description = "<a href='https://www.notion.so/1ae9366dcb7d818f8f47d05feb301bb8?pvs=4' target='_blank'>👉API 명세서 바로가기</a>")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<RecruitmentResponseDto>>> getAllRecruitment(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return BaseResponse.success(recruitmentService.getRecruitments(pageable));
    }

    @Operation(summary = "멤버 모집글 수정 기능",
            description = "<a href='https://www.notion.so/1ae9366dcb7d815eb344e698996bc823' target='_blank'>👉API 명세서 바로가기</a>")
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> modifyRecruitment(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @RequestBody RecruitmentRequestDto requestDto) {
        Long result = recruitmentService.modifyRecruitment(requestDto, id, user.getId());
        return BaseResponse.success(result);
    }
    @Operation(summary = "멤버 모집글 삭제 기능",
            description = "<a href='https://www.notion.so/1ae9366dcb7d81dea606ed6afa0cb688' target='_blank'>👉API 명세서 바로가기</a>")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteRecruitment(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        recruitmentService.deleteRecruitment(id, user.getId());
        return BaseResponse.success();
    }
}