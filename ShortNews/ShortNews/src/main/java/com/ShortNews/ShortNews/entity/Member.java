package com.ShortNews.ShortNews.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    private String id;
    private String platform;
    private String cre_date;
    private String email;
    private String salt;
    private String pw;
    private String nickname;
    private Integer alarm;

    @Builder
    public Member(String id, String platform, String cre_date, String email, String salt, String pw, String nickname, Integer alarm) {
        this.id = id;
        this.platform = platform;
        this.cre_date = cre_date;
        this.email = email;
        this.salt = salt;
        this.pw = pw;
        this.nickname = nickname;
        this.alarm = alarm;
    }
}