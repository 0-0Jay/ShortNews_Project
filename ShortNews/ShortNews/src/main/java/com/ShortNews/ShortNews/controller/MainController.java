package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.Token.JwtAuthenticationFilter;
import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.Token.TokenService;
import com.ShortNews.ShortNews.dto.NewsDto;
import com.ShortNews.ShortNews.dto.ReplyDto;
import com.ShortNews.ShortNews.dto.SessionDto;
import com.ShortNews.ShortNews.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/test")
    public String test() {
        return "성공";
    }

    @GetMapping("/main/news/{category}/{date}") // OK
    public Map<String, Object> mainNews(@PathVariable("category") String category, @PathVariable("date") String date, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);
        map = mainService.News(category, date, userPk);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/search/{keyword}")
    public Map<String, Object> mainSearch(@PathVariable("keyword") String keyword) {
        Map<String, Object> map = mainService.search(keyword);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/selectNews/{news_id}") // OK
    public NewsDto mainSelectNews(@PathVariable("news_id") String news_id, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);
        return mainService.selectNews(userPk, news_id);
    }

    @PatchMapping("/main/like") // OK
    public Map<String, Object> mainLike(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String news_id, before, after, token, userPk, reply_id;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);
        reply_id = (String)resultMap.get("reply_id");
        news_id = (String)resultMap.get("news_id");
        System.out.println(reply_id + " " + news_id);
        before = resultMap.get("before").toString();
        after = resultMap.get("after").toString();
        if (reply_id == null) {
            if (mainService.like(userPk, news_id, before, after, "news")) {
                map.put("status", HttpStatus.OK);
            } else {
                map.put("status", HttpStatus.BAD_REQUEST);
            }
        } else {
            if (mainService.like(userPk, reply_id, before, after, "reply")) {
                map.put("status", HttpStatus.OK);
            } else {
                map.put("status", HttpStatus.BAD_REQUEST);
            }
        }
        return map;
    }

    @PatchMapping("/main/bookmark") // OK
    public Map<String, Object> mainBookmark(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);
        String news_id = resultMap.get("news_id").toString();
        Boolean type = (Boolean) resultMap.get("type");
        mainService.Bookmark(userPk, news_id, type);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/link/{news_id}") // OK
    public Map<String, Object> mainLink(@PathVariable("news_id") String news_id) {
        Map<String, Object> map = new HashMap<>();
        String result = mainService.selectLink(news_id);
        if (result != null) {
            map.put("status", HttpStatus.OK);
            map.put("link", result);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("link", null);
        }

        return map;
    }

    @GetMapping("/main/reply/{news_id}")
    public Map<String, Object> mainReply(@PathVariable("news_id") String news_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.OK);
        map.put("replies", mainService.reply(news_id));
        return map;
    }

    @PatchMapping("/main/write")
    public Map<String, Object> mainWrite(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String upper_id = (String) resultMap.get("upper_id");
        ReplyDto rp = new ReplyDto(
                resultMap.get("news_id").toString(),
                session.getAttribute("id").toString(),
                resultMap.get("reply").toString(),
                upper_id
        );
        mainService.write(rp);
        map.put("status", HttpStatus.OK);
        map.put("replies", mainService.reply(rp.getNews_id()));
        return map;
    }

    @PatchMapping("/main/update")
    public Map<String, Object> mainUpdate(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String reply_id = (String) resultMap.get("reply_id");
        String text = (String) resultMap.get("text");
        String news_id = (String) resultMap.get("news_id");
        mainService.update(reply_id, text);
        map.put("status", HttpStatus.OK);
        map.put("replies", mainService.reply(news_id));
        return map;
    }

    @PatchMapping("/main/delete")
    public Map<String, Object> mainDelete(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String reply_id = (String) resultMap.get("reply_id");
        String news_id = (String) resultMap.get("news_id");
        mainService.delete(reply_id);
        map.put("status", HttpStatus.OK);
        map.put("replies", mainService.reply(news_id));
        return map;
    }

    @PostMapping("/main/report")
    public Map<String, Object> mainReport(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk, content, type, reply_id, news_id;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);
        content = (String) resultMap.get("content");
        type = (String) resultMap.get("type");
        reply_id = (String) resultMap.get("reply_id");
        news_id = (String) resultMap.get("news_id");
        mainService.report(userPk, content, type, reply_id, news_id);
        map.put("status", HttpStatus.OK);
        return map;
    }

}