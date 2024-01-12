package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/myActivity/like")
    public Map<String, Object> myActivityLike(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        map.put("newsLike", activityService.getLike(id));
        map.put("replyLike", activityService.getReplyLike(id));
        return map;
    }

    @GetMapping("/myActivity/dislike")
    public Map<String, Object> myActivityDisLike(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        map.put("result", activityService.getDisLike(id));
        map.put("replyDisLike", activityService.getReplyDisLike(id));
        return map;
    }

    @GetMapping("/myActivity/reply")
    public Map<String, Object> myActivityReply(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        map.put("status", HttpStatus.OK);
        map.put("result", activityService.reply(id));
        return map;
    }
}
