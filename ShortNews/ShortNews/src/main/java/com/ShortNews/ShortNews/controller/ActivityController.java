package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/myActivity/like")
    public Map<String, Object> myActivityLike(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        map.put("newsLike", activityService.getLikeOrDisLike(id, 1));
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/myActivity/dislike")
    public Map<String, Object> myActivityDisLike(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        map.put("newsDislike", activityService.getLikeOrDisLike(id, 0));
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/myActivity/reply")
    public Map<String, Object> myActivityReply(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        map.put("status", HttpStatus.OK);
        map.put("result", activityService.reply(id));
        return map;
    }
}
