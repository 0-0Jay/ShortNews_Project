package com.ShortNews.ShortNews.entity;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
public class Report {

    @Id
    private java.sql.Date time;
    private String id;
    private String type;
    private String content;
    private String reply_id;
    private String news_id;
}
