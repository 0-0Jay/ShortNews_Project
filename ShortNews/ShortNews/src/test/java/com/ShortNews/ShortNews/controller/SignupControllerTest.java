package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.dto.MemberDto;
import com.ShortNews.ShortNews.repository.SignupRepository;
import com.ShortNews.ShortNews.service.SignupService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SignupControllerTest {

    @Autowired
    private SignupService signupService;

    @Autowired
    private SignupRepository signupRepository;

    @Test
    void signupIdCheck() {
        //given
        String id = "jongho";

        //when
        boolean flag = signupService.idCheck(id);

        //then
        Assertions.assertThat(flag).isEqualTo(false);
    }

    @Test
    void signupEmailCheck() {
        //given
        String email = "whdgh6754@naver.com";

        //when
        String code = signupService.emailCheck(email);

        //then
        Assertions.assertThat(code).isEqualTo("");

    }

    @Test
    void join() throws NoSuchAlgorithmException {
        //given
        MemberDto memberDto = MemberDto.builder().id("5555").pw("123").email("yyyyyyy").nickname("").build();

        //when
        String id = signupService.join(memberDto);

        //then
        Assertions.assertThat(id).isEqualTo("5555");
    }

}