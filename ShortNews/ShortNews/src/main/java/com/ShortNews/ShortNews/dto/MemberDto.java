package com.ShortNews.ShortNews.dto;

import com.ShortNews.ShortNews.entity.Member;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberDto {

    private String id;
    private String email;
    private String nickname;
    private String pw;

    public MemberDto() {

    }

    @Builder
    public MemberDto(String id, String email, String nickname, String pw) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.pw = pw;
    }

    public Member toEntity(String platform, String salt, String pw, String nickname, String cre_date) {
        return Member.builder()
                .id(id)
                .platform(platform)
                .pw(pw)
                .salt(salt)
                .email(email)
                .nickname(nickname)
                .alarm(1)
                .cre_date(cre_date)
                .build();

    }
}