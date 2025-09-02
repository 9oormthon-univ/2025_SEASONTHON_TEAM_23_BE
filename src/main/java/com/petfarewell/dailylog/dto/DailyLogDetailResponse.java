package com.petfarewell.dailylog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyLogDetailResponse {
    private Long id;
    private LocalDate logDate;
    private String topic;
    private String title;
    private String content;
    private Integer mood;
    private String aiReflection;
}
