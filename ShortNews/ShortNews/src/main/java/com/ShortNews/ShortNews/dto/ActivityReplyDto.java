package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ActivityReplyDto {
    private String news_id;
    private String title;
    private String content;
    private String time;
    private String img;

    @Builder
    public ActivityReplyDto(String news_id, String title, String content, String time, String img) {
        this.news_id = news_id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.img = img;
    }
}