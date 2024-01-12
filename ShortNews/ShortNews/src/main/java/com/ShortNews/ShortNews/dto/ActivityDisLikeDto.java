package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ActivityDisLikeDto {

    private String id;
    private String content;
    private Integer type;
    private String time;

    public ActivityDisLikeDto() {

    }

    @Builder
    public ActivityDisLikeDto(String id, String content, Integer type, String time) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.time = time;
    }
}
