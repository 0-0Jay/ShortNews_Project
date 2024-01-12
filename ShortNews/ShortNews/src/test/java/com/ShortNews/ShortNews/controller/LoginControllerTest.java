package com.ShortNews.ShortNews.controller;


import com.ShortNews.ShortNews.repository.LoginRepository;
import com.ShortNews.ShortNews.service.LoginService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@SpringBootTest
class LoginControllerTest {

    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginRepository loginRepository;


    @Test
    void loginidCheck() {
        //given
        String id = "jongho";

        //when
        boolean flag = loginService.idCheck(id);

        //then
        Assertions.assertThat(flag).isEqualTo(true);
    }

    @Test
    void loginCheck() throws NoSuchAlgorithmException {
        //given
        String id = "jongho";
        String pw = "1234";

        //when
        loginService.loginCheck(id, pw);

        //then

    }

    @Test
    void loginFindPassword() {
        //given
        String id = "jongho";
        String email = "whdgh6754@naver.com";

        //when
        boolean flag = loginService.findPassword(id, email);

        //then
        Assertions.assertThat(flag).isEqualTo(true);

    }

    @Test
    void loginFindId() {
        //given
        String email = "whdgh6754@naver.com22";

        //when
        String id = loginService.findId(email);

        //then
        Assertions.assertThat(id).isEqualTo(null);
    }
}