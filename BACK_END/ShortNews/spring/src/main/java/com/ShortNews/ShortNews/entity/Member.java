package com.ShortNews.ShortNews.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Member {

    @Id
    private String mem_id;
    private String phone;
    private String salt;
    private String pw;
    private String nickname;
    private Integer alarm;
}
