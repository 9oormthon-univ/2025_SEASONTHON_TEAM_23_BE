package com.petfarewell.dailylog.dto;

import lombok.Data;

@Data
public class DailyLogUpdateRequest {
    private Integer mood;
    private String content;
    private boolean needAiReflection;
}
