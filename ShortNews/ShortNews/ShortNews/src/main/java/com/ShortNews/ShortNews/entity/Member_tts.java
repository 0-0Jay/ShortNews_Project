package com.ShortNews.ShortNews.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member_tts {

    @Id
    private String id;
    private String model_id;
    private String speed;

    @Builder
    public Member_tts(String id, String model_id, String speed) {
        this.id = id;
        this.model_id = model_id;
        this.speed = speed;
    }
}
