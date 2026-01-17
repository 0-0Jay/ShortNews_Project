package com.ShortNews.ShortNews.dto;

public interface AlarmInterface {
    java.sql.Timestamp getTime();
    String getLink();
    Integer getStatus();
    Integer getType();
    String getTarget_id();
    String getNickname();
}
