package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ReplyDto {
    private String reply_id;
    private String news_id;
    private String content;
    private String upper_id;
    private String id;
    private String good;
    private String bad;

    @Builder
    public ReplyDto(String reply_id, String news_id, String content, String upper_id, String id, String good, String bad) {
        this.reply_id = reply_id;
        this.news_id = news_id;
        this.content = content;
        this.upper_id = upper_id;
        this.id = id;
        this.good = good;
        this.bad = bad;
    }

    @Builder
    public ReplyDto(String news_id, String id, String content, String upper_id) {
        this.reply_id = "";
        this.news_id = news_id;
        this.content = content;
        this.upper_id = upper_id;
        this.id = id;
        this.good = "";
        this.bad = "";
    }
}