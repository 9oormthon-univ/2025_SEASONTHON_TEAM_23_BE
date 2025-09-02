package com.petfarewell.letter.entity;

import com.petfarewell.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "letters")
public class Letter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "content")
    private String content;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "tribute_count", nullable = false)
    private int tributeCount;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Letter(User user, String content, String photoUrl, boolean isPublic, int tributedCount, Instant createdAt) {
        this.user = user;
        this.content = content;
        this.photoUrl = photoUrl;
        this.isPublic = isPublic;
        this.tributeCount = tributedCount;
    }

    public void updateContent(String content) {
        this.content = content;
    }
    public void updatePhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public void updateIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void incrementTributeCount() {
        this.tributeCount++;
    }

    public void decrementTributeCount() {
        if (this.tributeCount > 0) {
            this.tributeCount--;
        }
    }
}
