package com.pickone.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResult {
    private Long id;
    private String url;
}

