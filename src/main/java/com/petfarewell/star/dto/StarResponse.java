package com.petfarewell.star.dto;

import lombok.Getter;

@Getter
public class StarResponse {
    private Long star;

    public StarResponse(Long star) {
        this.star = star;
    }
}
