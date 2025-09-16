package com.petfarewell.auth.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.petfarewell.dailylog.entity.DailyLog;
import com.petfarewell.letter.entity.Letter;
import com.petfarewell.letter.entity.LetterTribute;
import com.petfarewell.letter.entity.Notification;
import com.petfarewell.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "avatar_url")
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Letter> letters = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LetterTribute> tributes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyLog> dailyLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    public static User createFromKakao(Long kakaoId, String email, String nickname, String profileImageUrl) {
        User user = new User();
        user.setKakaoId(kakaoId);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setProfileImageUrl(profileImageUrl);
        return user;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}

