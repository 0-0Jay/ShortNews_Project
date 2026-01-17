package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class AlarmDto {

    private java.sql.Timestamp time;
    private String link;
    private Integer status;
    private Integer type;
    private String target_id;
    private String nickname;

    @Builder
    public AlarmDto(Timestamp time, String link, Integer status, Integer type, String target_id, String nickname) {
        this.time = time;
        this.link = link;
        this.status = status;
        this.type = type;
        this.target_id = target_id;
        this.nickname = nickname;
    }
}
