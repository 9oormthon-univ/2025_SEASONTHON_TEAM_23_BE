package com.petfarewell.letter.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tribute_messages")
public class TributeMessage {

    @Id
    @Column(name = "`key`")
    private String key;

    @Column(name = "text_ko")
    private String textKo;

    @Column(name = "sort_order")
    private int sortOrder;

    public TributeMessage(String key, String textKo, int sortOrder) {
        this.key = key;
        this.textKo = textKo;
        this.sortOrder = sortOrder;
    }
}
