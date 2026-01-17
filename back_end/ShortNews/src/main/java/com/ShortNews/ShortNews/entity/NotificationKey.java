package com.ShortNews.ShortNews.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@Embeddable
public class NotificationKey implements Serializable {


    @Column(name = "time", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT 'Asia/Seoul'")
    private java.sql.Timestamp time;

    private String id;

    public NotificationKey() {

    }

    @Builder
    public NotificationKey(Timestamp time, String id) {
        this.time = time;
        this.id = id;
    }
}
