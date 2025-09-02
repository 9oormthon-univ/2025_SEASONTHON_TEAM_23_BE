package com.petfarewell.dailylog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyLogSummaryResponse {
    private Long id;
    private LocalDate logDate;
    private String topic;
    private String preview;
    private Integer mood;
}
