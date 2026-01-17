package com.ShortNews.ShortNews.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
@Data
public class User_log {
    @EmbeddedId
    private User_logKey user_logKey;
    private java.sql.Date cre_date;

    @Builder
    public User_log(User_logKey user_logKey, Date cre_date) {
        this.user_logKey = user_logKey;
        this.cre_date = cre_date;
    }
}
