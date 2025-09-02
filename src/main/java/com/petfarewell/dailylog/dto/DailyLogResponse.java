package com.petfarewell.dailylog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class DailyLogResponse {
    private Long id;
}
