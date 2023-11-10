package com.ShortNews.ShortNews.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login/form")
    public String loginForm() {
        return "Login"; //Login.html로 이동
    }

    @PostMapping("/login/check")
    public String loginCheck() { //로그인 체크 해야함
        return "";
    }

}
