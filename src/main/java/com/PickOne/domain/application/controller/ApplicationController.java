package com.PickOne.domain.application.controller;

import com.PickOne.domain.application.dto.request.ApplicationRequestDto;
import com.PickOne.domain.application.service.ApplicationService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.security.model.entity.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal SecurityUser user,
            @RequestBody @Valid ApplicationRequestDto requestDto) {

        Long applyId=applicationService.applyToRecruitment(user.getUserId(), recruitmentId, requestDto);
        return BaseResponse.success(applyId);
    }
}
