package com.ShortNews.ShortNews.entity;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import java.sql.Date;

@Entity
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark {

    @EmbeddedId
    private BookmarkKey bookmarkKey;
    private java.sql.Date time;

    public Bookmark() {

    }

    @Builder
    public Bookmark(BookmarkKey bookmarkKey, Date time) {
        this.bookmarkKey = bookmarkKey;
        this.time = time;
    }

    @PrePersist
    public void prePersist() {
        this.time = new java.sql.Date(System.currentTimeMillis());
    }
}
