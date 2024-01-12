package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ActivityReplyDto {
    private String title;
    private String content;
    private String time;

    @Builder
    public ActivityReplyDto(String title, String content, String time) {
        this.title = title;
        this.content = content;
        this.time = time;
    }
}