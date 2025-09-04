package com.petfarewell.mypage.dto.response;

import lombok.Getter;

@Getter
public class UserActivitySummaryResponse {
    private final long dailyLogCount;
    private final long letterCount;
    private final long tributeCount;

    public UserActivitySummaryResponse(long dailyLogCount, long letterCount, long tributeCount) {
        this.dailyLogCount = dailyLogCount;
        this.letterCount = letterCount;
        this.tributeCount = tributeCount;
    }
}
