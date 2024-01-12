package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BeforeSelectNewsDto {

    private String news_id;
    private String title;
    private Integer views;
    private Integer like;
    private Integer dislike;
    private Integer reply;
    private Boolean bookmark;
    private String img;

    public BeforeSelectNewsDto() {

    }

    @Builder
    public BeforeSelectNewsDto(String news_id, String title, Integer views, Integer like, Integer dislike, Integer reply, Boolean bookmark, String img) {
        this.news_id = news_id;
        this.title = title;
        this.views = views;
        this.like = like;
        this.dislike = dislike;
        this.reply = reply;
        this.bookmark = bookmark;
        this.img = img;
    }
}
