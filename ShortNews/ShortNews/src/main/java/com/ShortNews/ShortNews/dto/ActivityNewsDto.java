package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ActivityNewsDto {

    private String news_id;
    private String cate_id;
    private String title;
    private Integer views;
    private Integer bookmark;
    private Integer like;
    private Integer dislike;
    private Integer reply;
    private String img;

    @Builder
    public ActivityNewsDto(String news_id, String cate_id, String title, Integer views, Integer bookmark, Integer like, Integer dislike, Integer reply, String img) {
        this.news_id = news_id;
        this.cate_id = cate_id;
        this.title = title;
        this.views = views;
        this.bookmark = bookmark;
        this.like = like;
        this.dislike = dislike;
        this.reply = reply;
        this.img = img;
    }
}