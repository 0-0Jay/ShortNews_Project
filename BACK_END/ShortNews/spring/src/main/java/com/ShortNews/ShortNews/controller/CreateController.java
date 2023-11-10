package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CreateController { //컨트롤러 -> 서비스 -> 리포지토리

    @Autowired
    private MemberService memberService;

    @GetMapping("/create")
    public String create() {
        return "Create";
    }

    @PostMapping("/create/check")
    public String createCheck(Member member) { //회원가입 체크
        memberService.join(member);
        return "";
    }
}
