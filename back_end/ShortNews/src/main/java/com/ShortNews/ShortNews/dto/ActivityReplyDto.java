package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ActivityReplyDto {
    private String news_id;
    private String title;
    private String imgs;
    private String reply_id;
    private String id;
    private String content;
    private String nickname;
    private String low_rid;
    private String low_uid;
    private String low_content;
    private String low_nickname;

    public ActivityReplyDto(){};

    @Builder
    public ActivityReplyDto(String news_id, String title, String imgs, String reply_id, String id, String content, String nickname, String low_rid, String low_uid, String low_content, String low_nickname) {
        this.news_id = news_id;
        this.title = title;
        this.imgs = imgs;
        this.reply_id = reply_id;
        this.id = id;
        this.content = content;
        this.nickname = nickname;
        this.low_rid = low_rid;
        this.low_uid = low_uid;
        this.low_content = low_content;
        this.low_nickname = low_nickname;
    }
}