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
public class Reason {

    @Id
    private Integer reason_id;
    private String content;

    @Builder
    public Reason(Integer reason_id, String content) {
        this.reason_id = reason_id;
        this.content = content;
    }
}
