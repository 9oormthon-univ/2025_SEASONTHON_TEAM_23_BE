package com.petfarewell.dailylog.dto;

import lombok.Data;

@Data
public class UpdateDailyLogRequest {
    private Integer mood;
    private String title;
    private String content;
    private String topic;
    private Boolean regenerateReflection;
}
