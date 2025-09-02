package com.petfarewell.dailylog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyTopicResponse {
    private String topic;
    private LocalDate date;
}
