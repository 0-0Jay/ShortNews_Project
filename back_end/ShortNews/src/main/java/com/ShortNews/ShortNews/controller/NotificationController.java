package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RestController
@CrossOrigin(originPatterns = {"http://localhost:8090",
        "http://localhost:3000",
        "https://www.shortnews.kr",
        "http://www.shortnews.kr",
        "https://shortnews.kr",
        "http://shortnews.kr",
        "http://api.shortnews.kr",
        "https://api.shortnews.kr",
        "http://3.35.26.160:8090",
        "http://3.35.26.160"}, allowedHeaders = "Authorization", allowCredentials = "true")
public class NotificationController {

    private final NotificationService notificationService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    public static Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/sub", produces = "text/event-stream")
    @CrossOrigin(originPatterns = {"http://localhost:8090",
            "http://localhost:3000",
            "https://www.shortnews.kr",
            "http://www.shortnews.kr",
            "https://shortnews.kr",
            "http://shortnews.kr",
            "http://api.shortnews.kr",
            "https://api.shortnews.kr",
            "http://3.35.26.160:8090",
            "http://3.35.26.160"}, allowedHeaders = "Authorization", allowCredentials = "true")
    public SseEmitter subscribe(HttpServletRequest request) {

        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);
        SseEmitter sseEmitter = notificationService.subscribe(userPk);

        return sseEmitter;
    }
}