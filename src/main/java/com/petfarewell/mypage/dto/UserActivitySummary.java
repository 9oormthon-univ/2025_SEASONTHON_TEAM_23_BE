package com.petfarewell.mypage.dto;

import lombok.Getter;

@Getter
public class UserActivitySummary {
    private final long dailyLogCount;
    private final long letterCount;
    private final long tributeCount;

    public UserActivitySummary(long dailyLogCount, long letterCount, long tributeCount) {
        this.dailyLogCount = dailyLogCount;
        this.letterCount = letterCount;
        this.tributeCount = tributeCount;
    }
}
