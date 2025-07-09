package com.pickone.global.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "S3 버킷", description = "파일 업로드 및 조회 API")
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "파일 업로드", description = "Multipart 파일과 텍스트를 함께 업로드합니다.")
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("imgText") String imgText
    ) {
        try {
            // 파라미터 이름(file, imgText)에 맞춰서 호출
            s3Service.saveFile(file, imgText);
            return ResponseEntity.ok("파일 업로드 성공");
        } catch (Exception e) {
            // 로그를 남기고 400 리턴
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("파일 업로드 실패: " + e.getMessage());
        }
    }

    /** 모든 업로드된 파일 목록 조회 */
    @Operation(summary = "전체 파일 목록 조회", description = "업로드된 모든 파일 정보를 반환합니다.")
    @GetMapping("/files")
    public ResponseEntity<List<FileResponse>> listAll() {
        return ResponseEntity.ok(s3Service.listFiles());
    }

    /** 특정 ID 의 파일 정보(= public URL) 조회 */
    @Operation(summary = "단일 파일 조회", description = "ID를 통해 특정 파일 정보를 조회합니다.")
    @GetMapping("/files/{id}")
    public ResponseEntity<FileResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(s3Service.getFile(id));
    }
}

