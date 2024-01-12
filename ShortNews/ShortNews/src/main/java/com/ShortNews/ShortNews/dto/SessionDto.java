package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class SessionDto {

    private String nickname;
    private String email;
    private String id;
    private List<String> category;
    private String model;
    private String speed;
    private String platform;

    public SessionDto() {

    }


    @Builder
    public SessionDto(String nickname, String email, String id, List<String> category, String model, String speed, String platform) {
        this.nickname = nickname;
        this.email = email;
        this.id = id;
        this.category = category;
        this.model = model;
        this.speed = speed;
        this.platform = platform;
    }
}
