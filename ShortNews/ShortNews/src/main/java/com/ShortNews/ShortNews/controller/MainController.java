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

    @GetMapping("/main/news/{category}/{date}")
    public Map<String, Object> mainNews(@PathVariable("category") String category, @PathVariable("date") String date) {
        Map<String, Object> map = mainService.News(category, date);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/search/{keyword}")
    public Map<String, Object> mainSearch(@PathVariable("keyword") String keyword) {
        Map<String, Object> map = mainService.search(keyword);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/selectNews/{news_id}")
    public NewsDto mainSelectNews(@PathVariable("news_id") String news_id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        return mainService.selectNews(id, news_id);
    }

    @PatchMapping("/main/like")
    public Map<String, Object> mainLike(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        String news_id = resultMap.get("news_id").toString();
        String type = resultMap.get("type").toString();
        if (mainService.like(id, news_id, type)) {
            map.put("status", HttpStatus.OK);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PatchMapping("/main/dislike")
    public Map<String, Object> mainDisLike(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        String news_id = resultMap.get("news_id").toString();
        String type = resultMap.get("type").toString();
        if (mainService.disLike(id, news_id, type)) {
            map.put("status", HttpStatus.OK);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PatchMapping("/main/bookmark")
    public Map<String, Object> mainBookmark(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = session.getAttribute("id").toString();
        String news_id = resultMap.get("news_id").toString();
        mainService.insertBookmark(id, news_id);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/link/{news_id}")
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
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        String upper_id = (String) resultMap.get("upper_id");
        ReplyDto rp = new ReplyDto(
                resultMap.get("news_id").toString(),
                id,
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

}