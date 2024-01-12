package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LikeDto {

    private String title;
    private Integer type;
    private String date;

    public LikeDto() {

    }

    @Builder
    public LikeDto(String title, Integer type, String date) {
        this.title = title;
        this.type = type;
        this.date = date;
    }
}
