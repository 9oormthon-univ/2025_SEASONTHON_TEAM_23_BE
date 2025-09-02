package com.petfarewell.letter.dto.response;

import com.petfarewell.auth.entity.User;
import com.petfarewell.letter.entity.LetterTribute;
import lombok.Getter;

@Getter
public class TributeResponse {

    private final Long tributeId;
    private final String messageContent;
    private final AuthorDto author;

    private TributeResponse(Long tributeId, String messageContent, AuthorDto author) {
        this.tributeId = tributeId;
        this.messageContent = messageContent;
        this.author = author;
    }

    public static TributeResponse from(LetterTribute tribute) {
        return new TributeResponse(
                tribute.getId(),
                tribute.getMessage().getTextKo(),
                AuthorDto.from(tribute.getUser())
        );
    }

    @Getter
    private static class AuthorDto {
        private final Long userId;
        private final String nickname;

        private AuthorDto(Long userId, String nickname) {
            this.userId = userId;
            this.nickname = nickname;
        }

        public static AuthorDto from(User user) {
            return new AuthorDto(user.getId(), user.getNickname());
        }
    }
}