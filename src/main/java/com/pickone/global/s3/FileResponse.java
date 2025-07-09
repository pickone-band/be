package com.pickone.global.s3;

public record FileResponse(
        Long id,
        String imgText,
        String imgUrl  // public URL
) {}