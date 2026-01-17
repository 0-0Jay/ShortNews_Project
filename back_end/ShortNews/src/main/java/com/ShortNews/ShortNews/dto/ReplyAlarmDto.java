package com.ShortNews.ShortNews.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ReplyAlarmDto {
    private String id;
    private String reply_id;
    private int alarm;

    @Builder
    public ReplyAlarmDto(String id, String reply_id, int alarm) {
        this.id = id;
        this.reply_id = reply_id;
        this.alarm = alarm;
    }
}
