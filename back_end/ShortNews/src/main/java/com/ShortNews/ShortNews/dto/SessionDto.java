package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class SessionDto {

    private String nickname;
    private String phone;
    private String id;
    private List<String> category;
    private String model;
    private String speed;
    private String platform;
    private Integer alarm;

    public SessionDto() {

    }


    @Builder
    public SessionDto(String nickname, String phone, String id, List<String> category, String model, String speed, String platform, Integer alarm) {
        this.nickname = nickname;
        this.phone = phone;
        this.id = id;
        this.category = category;
        this.model = model;
        this.speed = speed;
        this.platform = platform;
        this.alarm = alarm;
    }
}
