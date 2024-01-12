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
public class News {

    @Id
    private String news_id;
    private String cate_id;
    private String title;
    private String content;
    private Integer views;
    private String url;
    private String imgs;

    @Builder
    public News(String news_id, String cate_id, String title, String content, Integer views, String url, String imgs) {
        this.news_id = news_id;
        this.cate_id = cate_id;
        this.title = title;
        this.content = content;
        this.views = views;
        this.url = url;
        this.imgs = imgs;
    }
}
