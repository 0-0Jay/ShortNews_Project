package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class NewsListDto {

    private String news_id;
    private String cate_id;
    private String title;
    private Integer views;
    private Integer like;
    private Integer dislike;
    private Integer reply;
    private Integer bookmark;
    private String imgs;
    private Integer report;

    public NewsListDto() {

    }

    @Builder
    public NewsListDto(String news_id, String title, Integer views, Integer like, Integer dislike, Integer reply, Integer bookmark, String imgs, Integer report) {
        this.news_id = news_id;
        this.title = title;
        this.views = views;
        this.like = like;
        this.dislike = dislike;
        this.reply = reply;
        this.bookmark = bookmark;
        this.imgs = imgs;
        this.report = report;
    }
}
