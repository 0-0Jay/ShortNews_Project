package com.ShortNews.ShortNews.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Getter
public class Notification {

    @EmbeddedId
    private NotificationKey notificationKey;
    private String link;
    private Integer status;
    private Integer type;
    private String target_id;

    public Notification() {

    }
    @Builder
    public Notification(NotificationKey notificationKey, String link, Integer status, Integer type, String target_id) {
        this.notificationKey = notificationKey;
        this.link = link;
        this.status = status;
        this.type = type;
        this.target_id = target_id;
    }
}
