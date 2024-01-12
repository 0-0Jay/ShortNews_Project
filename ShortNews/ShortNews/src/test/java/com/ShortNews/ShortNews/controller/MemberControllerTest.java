package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.repository.MemberRepository;
import com.ShortNews.ShortNews.service.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberControllerTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    void memberUpdateNickname() {
        String id = "jongho";
        String nickname = "20240102";

        System.out.println(memberService.updateNickname(id, nickname));

        List<Member> list = memberRepository.findByNickname("20240102");
        Assertions.assertThat(list.isEmpty()).isEqualTo(false);
    }
}