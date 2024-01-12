package com.ShortNews.ShortNews.Token;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Refreshtoken {

    @Id
    private String id;
    private String refreshtoken;

    @Builder
    public Refreshtoken(String id, String refreshtoken) {
        this.id = id;
        this.refreshtoken = refreshtoken;
    }
}
