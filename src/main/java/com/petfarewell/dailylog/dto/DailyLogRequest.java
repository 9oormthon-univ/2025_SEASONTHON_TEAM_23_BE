package com.petfarewell.dailylog.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DailyLogRequest {
    private LocalDate logDate;
    private Integer mood;
    private String content;
    private boolean needAiReflection = true;
}
