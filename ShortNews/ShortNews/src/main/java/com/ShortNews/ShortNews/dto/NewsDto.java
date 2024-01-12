package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class NewsDto {

    private String title;
    private String content;
    private String imgs;
    private Integer views;
    private Integer like;
    private Integer dislike;
    private Integer type;
    private Boolean bookmark;

    public NewsDto() {

    }

    @Builder
    public NewsDto(String title, String content, String imgs, Integer views, Integer like, Integer dislike, Integer type, Boolean bookmark) {
        this.title = title;
        this.content = content;
        this.imgs = imgs;
        this.views = views;
        this.like = like;
        this.dislike = dislike;
        this.type = type;
        this.bookmark = bookmark;
    }
}
