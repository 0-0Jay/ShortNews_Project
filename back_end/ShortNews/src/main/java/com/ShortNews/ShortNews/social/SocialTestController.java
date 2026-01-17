package com.ShortNews.ShortNews.social;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SocialTestController {

    @GetMapping("/login")
    public String login() {
        return "Login";
    }
}
