package com.ShortNews.ShortNews.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Embeddable
public class User_logKey implements Serializable {
    private String id;
    @Column(name = "time", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT 'Asia/Seoul'")
    private Timestamp cre_date;
    private String news_id;
    public User_logKey(){

    }

    @Builder
    public User_logKey(String id, Timestamp cre_date, String news_id) {
        this.id = id;
        this.cre_date = cre_date;
        this.news_id = news_id;
    }
}
