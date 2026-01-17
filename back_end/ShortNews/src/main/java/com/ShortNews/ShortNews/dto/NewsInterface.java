package com.ShortNews.ShortNews.dto;

public interface NewsInterface {
    String getNews_id();
    String getCate_id();
    String getTitle();
    String getContent();
    String getImgs();
    Integer getViews();
    Integer getReply();
    Integer getGood();
    Integer getBad();
    Integer getBookmark();
    Integer getType();
}
