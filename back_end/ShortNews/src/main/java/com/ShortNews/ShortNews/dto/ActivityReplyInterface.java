package com.ShortNews.ShortNews.dto;

public interface ActivityReplyInterface {
    String getNews_id();
    String getTitle();
    String getImgs();
    String getLowrid(); // 유저가 쓴 댓글 아이디
    String getLowuid(); // 유저 아이디
    String getLownick(); // 유저 닉네임
    String getLowcnt(); // 유저가 쓴 댓글 내용
    String getUprid(); // 상위 댓글 아이디 -> null이면 lowrid가 상위댓글
    String getUpuid(); // 상위 댓글 작성자 아이디
    String getUpnick(); // 상위 댓글 작성자 닉네임
    String getUpcnt(); // 상위 댓글 내용
}