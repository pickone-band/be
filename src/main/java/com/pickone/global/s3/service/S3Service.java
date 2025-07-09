package com.pickone.global.s3.service;

import com.pickone.global.s3.dto.FileResponse;
import com.pickone.global.s3.dto.FileUploadResult;
import com.pickone.global.s3.entity.TestFileEntity;
import com.pickone.global.s3.repository.TestFileRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Uploader s3Uploader;
    private final TestFileRepository testFileRepository;

    @Transactional
    public FileUploadResult saveFile(MultipartFile imgFile, String imgText) throws IOException {
        if (imgFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 필요합니다.");
        }

        String storedFileName = s3Uploader.upload(imgFile, "profiles"); // S3에 업로드된 URL 반환

        TestFileEntity testFileEntity = new TestFileEntity();
        testFileEntity.setImgText(imgText);
        testFileEntity.setImgUrl(storedFileName);
        TestFileEntity saved = testFileRepository.save(testFileEntity);

        return new FileUploadResult(saved.getId(), storedFileName);
    }

    /** 단일 파일 조회 */
    @Transactional(readOnly = true)
    public FileResponse getFile(Long id) {
        TestFileEntity e = testFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + id));
        String stored = e.getImgUrl();
        String publicUrl;

        if (stored.startsWith("http://") || stored.startsWith("https://")) {
            // 이미 전체 URL이 저장되어 있으면, 그대로 반환
            publicUrl = stored;
        } else {
            // S3 키(key)만 저장된 경우에만 getPublicUrl 호출
            publicUrl = s3Uploader.getPublicUrl(stored);
        }

        return new FileResponse(e.getId(), e.getImgText(), publicUrl);
    }

    /** 전체 파일 목록 조회 */
    @Transactional(readOnly = true)
    public List<FileResponse> listFiles() {
        return testFileRepository.findAll().stream()
                .map(e -> {
                    String stored = e.getImgUrl();
                    String publicUrl;

                    if (stored.startsWith("http://") || stored.startsWith("https://")) {
                        // 이미 전체 URL이 저장되어 있으면 그대로 사용
                        publicUrl = stored;
                    } else {
                        // 키만 저장된 경우 S3에서 퍼블릭 URL 생성
                        publicUrl = s3Uploader.getPublicUrl(stored);
                    }

                    return new FileResponse(e.getId(), e.getImgText(), publicUrl);
                })
                .collect(Collectors.toList());
    }

}
