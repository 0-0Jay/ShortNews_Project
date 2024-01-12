package com.ShortNews.ShortNews.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class BookmarkKey implements Serializable {

    private String id;
    private String news_id;

    public BookmarkKey(){

    }

    @Builder
    public BookmarkKey(String id, String news_id) {
        this.id = id;
        this.news_id = news_id;
    }


}
