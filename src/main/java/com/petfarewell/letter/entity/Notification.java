package com.petfarewell.letter.entity;

import com.petfarewell.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private Long unreadTributeCount;

    public void incrementTributeCount() {
        this.unreadTributeCount++;
    }

    public void resetTributeCount() {
        this.unreadTributeCount = 0L;
    }

    public Notification(User user) {
        this.user = user;
        this.unreadTributeCount = 0L;
    }
}
