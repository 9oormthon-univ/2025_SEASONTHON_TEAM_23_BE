package com.petfarewell.dailylog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoodCountResponse {
    private int bestMoodCount;
    private int goodMoodCount;
    private int dailyCount;
}
