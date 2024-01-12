package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BookmarkDto {
    private String id;
    private String cate_id;
    private String news_id;
    private String title;

    @Builder
    public BookmarkDto(String id, String cate_id, String news_id, String title) {
        this.id = id;
        this.cate_id = cate_id;
        this.news_id = news_id;
        this.title = title;
    }
}