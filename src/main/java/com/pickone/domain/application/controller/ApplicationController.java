package com.pickone.domain.application.controller;

import com.pickone.domain.application.dto.request.ApplicationRequestDto;
import com.pickone.domain.application.dto.response.ApplicationResponseDto;
import com.pickone.domain.application.service.ApplicationService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.security.entity.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "멤버 신청 API", description = "멤버 모집글에 대한 신청 API")
@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "멤버 모집글 신청",
            description = "<a href='https://www.notion.so/1ae9366dcb7d81fabbaaf554418faf88' target='_blank'>👉API 명세서 바로가기</a>")
    @PostMapping("/apply/{recruitmentId}")
    public ResponseEntity<BaseResponse<Long>> applyToRecruitment(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody ApplicationRequestDto requestDto) {

        Long applyId=applicationService.applyToRecruitment(user.getId(), recruitmentId, requestDto);
        return BaseResponse.success(applyId);
    }

    @Operation(summary = "멤버 모집글 신청 조회",
            description = "<a href='https://www.notion.so/1bc9366dcb7d80b495bce914722b5380' target='_blank'>👉API 명세서 바로가기</a>")
    @GetMapping("/apply/{recruitmentId}")
    public ResponseEntity<BaseResponse<ApplicationResponseDto>> getMyApplications(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal UserPrincipal user){
        ApplicationResponseDto applicationResponseDto= applicationService.getMyApplication(user.getId(),recruitmentId);
        return BaseResponse.success(applicationResponseDto);
    }

    @Operation(summary = "멤버 모집글 신청글 수정",
            description = "<a href='https://www.notion.so/1ae9366dcb7d814e9dd0cb651b35d752' target='_blank'>👉API 명세서 바로가기</a>")
    @PatchMapping("/apply/{recruitmentId}")
    public ResponseEntity<BaseResponse<Void>> modifyMyApplications(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody ApplicationRequestDto requestDto){
        applicationService.modifyApplication(user.getId(),recruitmentId,requestDto);
        return BaseResponse.success();
    }

    @Operation(summary = "멤버 모집글 신청글 취소",
            description = "<a href='https://www.notion.so/1bc9366dcb7d8000a9b8c7f0d0e6929c' target='_blank'>👉API 명세서 바로가기</a>")
    @PatchMapping("/apply/{recruitmentId}/cancel")
    public ResponseEntity<BaseResponse<Void>> cancelMyApplications(
            @PathVariable Long recruitmentId,
            @AuthenticationPrincipal UserPrincipal user){
        applicationService.cancelMyApplication(user.getId(),recruitmentId);
        return BaseResponse.success();
    }

}
