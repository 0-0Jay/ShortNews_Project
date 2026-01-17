package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.Token.JwtAuthenticationFilter;
import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.Token.TokenService;
import com.ShortNews.ShortNews.dto.NewsDto;
import com.ShortNews.ShortNews.dto.ReplyDto;
import com.ShortNews.ShortNews.dto.SessionDto;
import com.ShortNews.ShortNews.service.MainService;
import com.ShortNews.ShortNews.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MainController {
    @Autowired
    private MainService mainService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/main/news/{category}/{date}") // OK
    public Map<String, Object> mainNews(@PathVariable("category") String category, @PathVariable("date") String date, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);
        map = mainService.News(category, date, userPk);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/search")
    public Map<String, Object> mainSearch(@RequestParam("q") String keyword, @RequestParam("s") int s, @RequestParam("e") int e, HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        Map<String, Object> map = mainService.search(id, keyword, s, e);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/selectNews/{news_id}") // OK
    public Map<String, Object> mainSelectNews(@PathVariable("news_id") String news_id, HttpServletRequest request) throws IOException {
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = "";
        if (token != null) userPk = jwtTokenProvider.getUserPk(token);
        NewsDto result = mainService.selectNews(userPk, news_id);
        Map<String, Object> map = new HashMap<>();
        if (result != null) {
            map.put("status", HttpStatus.OK);
            map.put("news", result);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PatchMapping("/main/like") // OK
    public Map<String, Object> mainLike(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);

        String reply_id = (String) resultMap.get("reply_id");
        String news_id = (String) resultMap.get("news_id");
        int like = (int) resultMap.get("like");

        mainService.like(userPk, news_id, reply_id, like);
        map.put("status", HttpStatus.OK);
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

    @GetMapping("/main/selectNews/{news_id}/reply")
    public Map<String, Object> mainReply(@PathVariable("news_id") String news_id, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);
        map.put("replies", mainService.reply(news_id, userPk));
        map.put("status", HttpStatus.OK);
        return map;
    }

    @PatchMapping("/main/write")
    public Map<String, Object> mainWrite(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);

        String upper_id = (String) resultMap.get("upper_id");
        String upper_user = (String) resultMap.get("upper_user");
        ReplyDto rp = new ReplyDto(
                resultMap.get("news_id").toString(),
                userPk,
                resultMap.get("reply").toString(),
                upper_id
        );
        mainService.write(rp, upper_user);

        map.put("status", HttpStatus.OK);
        map.put("replies", mainService.reply(rp.getNews_id(), userPk));
        return map;
    }

    @PatchMapping("/main/update")
    public Map<String, Object> mainUpdate(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String reply_id = (String) resultMap.get("reply_id");
        String text = (String) resultMap.get("text");
        String news_id = (String) resultMap.get("news_id");
        mainService.update(reply_id, text);
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);

        map.put("status", HttpStatus.OK);
        map.put("replies", mainService.reply(news_id, userPk));
        return map;
    }

    @PatchMapping("/main/delete")
    public Map<String, Object> mainDelete(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String reply_id = (String) resultMap.get("reply_id");
        String news_id = (String) resultMap.get("news_id");
        mainService.delete(reply_id);
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);
        map.put("status", HttpStatus.OK);
        map.put("replies", mainService.reply(news_id, userPk));
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


        map.put("report", mainService.report(userPk, content, type, reply_id, news_id));
        map.put("status", HttpStatus.OK);
        return map;
    }

    @GetMapping("/main/alarm")
    public Map<String, Object> mainAlarm(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        String userPk = jwtTokenProvider.getUserPk(token);
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.OK);
        map.put("alarm", notificationService.getAlarm(userPk));
        return map;
    }

    @PatchMapping("/main/alarm/switch")
    public Map<String, Object> mainAlarmSwitch(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        int alarm = (int) resultMap.get("alarm");
        if (alarm == 0 || alarm == 1) {
            mainService.alarmSwitch(userPk, alarm);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PatchMapping("/main/alarm/select")
    public Map<String, Object> mainAlarmSelect(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        String time = resultMap.get("time").toString();
        String news_id = resultMap.get("news_id").toString();
        if (time == null || news_id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            mainService.alarmSelect(userPk, time, news_id);
            map.put("status", HttpStatus.OK);
        }

        return map;
    }

    @DeleteMapping("/main/alarm/remove")
    public Map<String, Object> mainAlarmRemove(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        String time = resultMap.get("time").toString();
        String news_id = resultMap.get("news_id").toString();
        if (time == null || news_id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            mainService.alarmRemove(userPk, time, news_id);
            map.put("status", HttpStatus.OK);
        }

        return map;
    }

    @DeleteMapping("/main/alarm/drop")
    public Map<String, Object> mainAlarmDrop(HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        mainService.alarmDrop(userPk);
        map.put("status", HttpStatus.OK);

        return map;
    }

    @GetMapping("/shareNews/{news_id}") // OK
    public Map<String, Object> mainShareNews(@PathVariable("news_id") String news_id, @RequestParam(name = "guest") String guest) {
        NewsDto result = mainService.shareNews(news_id);
        Map<String, Object> map = new HashMap<>();
        if (guest.equals("guest") && result != null) {
            map.put("status", HttpStatus.OK);
            map.put("news", result);
            map.put("link", mainService.selectLink(news_id));
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }
}