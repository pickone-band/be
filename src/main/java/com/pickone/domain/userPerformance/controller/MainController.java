package com.pickone.domain.userPerformance.controller;

import com.pickone.domain.user.dto.UserResponse;
import com.pickone.domain.userPerformance.service.MainPageService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "메인페이지", description = "메인페이지 관련")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MainController {

    private final MainPageService mainPageService;

    @Operation(summary = "메인페이지 프로필 보여주기", description = "메인에서 프로필을 보여줍니다")
    @GetMapping("/main")
    public ResponseEntity<BaseResponse<List<UserResponse>>> getMainUsers() {
        return BaseResponse.success(mainPageService.getUsersFrom1To10());
    }
}
