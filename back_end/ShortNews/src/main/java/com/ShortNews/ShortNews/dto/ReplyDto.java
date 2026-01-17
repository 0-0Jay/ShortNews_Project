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
    private int good;
    private int bad;
    private String nickname;
    private int type;
    private int report;
    private int edited;

    @Builder
    public ReplyDto(String reply_id, String news_id, String content, String upper_id, String id, int good, int bad, String nickname, int type, int report, int edited) {
        this.reply_id = reply_id;
        this.news_id = news_id;
        this.content = content;
        this.upper_id = upper_id;
        this.id = id;
        this.good = good;
        this.bad = bad;
        this.nickname = nickname;
        this.type = type;
        this.report = report;
        this.edited = edited;
    }

    @Builder
    public ReplyDto(String news_id, String id, String content, String upper_id) {
        this.reply_id = "";
        this.news_id = news_id;
        this.content = content;
        this.upper_id = upper_id;
        this.id = id;
        this.good = 0;
        this.bad = 0;
        this.report = 0;
        this.edited = 0;
    }
}