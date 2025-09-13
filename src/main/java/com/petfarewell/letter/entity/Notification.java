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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", unique = true)
    private Letter letter;

    @Column
    private Long unreadTributeCount;

    public void incrementTributeCount() {
        this.unreadTributeCount++;
    }

    public void resetTributeCount() {
        this.unreadTributeCount = 0L;
    }

    public Notification(User user, Letter letter) {
        this.user = user;
        this.letter = letter;
        this.unreadTributeCount = 0L;
    }
}
