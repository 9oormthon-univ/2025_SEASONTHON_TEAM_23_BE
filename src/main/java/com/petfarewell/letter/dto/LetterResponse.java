package com.petfarewell.letter.dto;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.Letter;
import lombok.Getter;
import java.util.Date;

@Getter
public class LetterResponse {

    private final Long letterId;
    private final String content;
    private final String photoUrl;
    private final int tributeCount;
    private final Date createdAt;
    private final AuthorDto author;

    public static LetterResponse from(Letter letter) {
        return new LetterResponse(
                letter.getId(),
                letter.getContent(),
                letter.getPhotoUrl(),
                letter.getTributeCount(),
                letter.getCreatedAt(),
                AuthorDto.from(letter.getUser())
        );
    }

    private LetterResponse(Long letterId, String content, String photoUrl, int tributeCount, Date createdAt, AuthorDto author) {
        this.letterId = letterId;
        this.content = content;
        this.photoUrl = photoUrl;
        this.tributeCount = tributeCount;
        this.createdAt = createdAt;
        this.author = author;
    }

    @Getter
    private static class AuthorDto {
        private final Long userId;
        private final String nickname;
        private final String profileImageUrl;

        public static AuthorDto from(User user) {
            return new AuthorDto(user.getId(), user.getNickname(), user.getProfileImageUrl());
        }

        private AuthorDto(Long userId, String nickname, String profileImageUrl) {
            this.userId = userId;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
        }
    }
}
