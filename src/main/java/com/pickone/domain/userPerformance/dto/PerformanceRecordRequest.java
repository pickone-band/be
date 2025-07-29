package com.pickone.domain.userPerformance.dto;

import java.time.LocalDate;
import java.util.List;

public record PerformanceRecordRequest(
    String title,
    LocalDate date,
    String teamName,
    String thumbnailUrl,
    String videoUrl,
    String description,
    List<SongDto> songs){

}

