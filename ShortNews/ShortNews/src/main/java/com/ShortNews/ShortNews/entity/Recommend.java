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
public class Recommend {

    @Id
    private String recom_id;
    private String id;
    private String reply_id;
    private String news_id;
    private Integer type;

    @Builder
    public Recommend(String recom_id, String id, String reply_id, String news_id, Integer type) {
        this.recom_id = recom_id;
        this.id = id;
        this.reply_id = reply_id;
        this.news_id = news_id;
        this.type = type;
    }
}
