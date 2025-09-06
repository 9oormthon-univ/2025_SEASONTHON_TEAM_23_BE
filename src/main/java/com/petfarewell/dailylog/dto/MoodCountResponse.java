package com.petfarewell.dailylog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoodCountResponse {
    private int bestMoodCount;
    private int dailyCount;
}
