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
public class Reply {
    @Id
    private String reply_id;
    private String news_id;
    private String id;
    private String content;
    private String upper_id;

    @Builder
    public Reply(String reply_id, String news_id, String id, String content, String upper_id) {
        this.reply_id = reply_id;
        this.news_id = news_id;
        this.id = id;
        this.content = content;
        this.upper_id = upper_id;
    }
}
