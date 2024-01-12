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
public class Reason {

    @Id
    private Integer id;
    private String content;
    private String cre_date;

    @Builder
    public Reason(Integer id, String content, String cre_date) {
        this.id = id;
        this.content = content;
        this.cre_date = cre_date;
    }
}
