package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/member/mypage") // OK
    public Map<String, Object> memberMypage(HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        map.put("status", HttpStatus.OK);

        return map;
    }

    @PatchMapping("/member/mypage/updatePassword") // OK
    public Map<String, Object> memberMypageUpdatePassword(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) throws NoSuchAlgorithmException {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        if (memberService.updatePw(userPk, resultMap.get("origin_pw").toString(), resultMap.get("new_pw").toString())) {
            map.put("status", HttpStatus.OK);
            map.put("flag", true);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("flag", false);
        }

        return map;
    }

//    @GetMapping("/member/category")
//    public Map<String, Object> memberCategory(HttpServletRequest request) {
//        Map<String, Object> map = new HashMap<>();
//        HttpSession session = request.getSession();
//        List<String> category = (List<String>) session.getAttribute("category");
//        map.put("categorylist", category);
//        map.put("status", HttpStatus.OK);
//        return map;
//    }

    @PatchMapping("/member/updateNickname") // OK
    public Map<String, Object> memberUpdateNickname(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);
        String nickname = (String) resultMap.get("nickname");
        System.out.println(userPk + " " + nickname);
        if (memberService.updateNickname(userPk, nickname)) {
            map.put("status", HttpStatus.OK);
            map.put("message", "");
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "닉네임이 중복됩니다.");
        }
        return map;
    }

    @DeleteMapping("/member/delete") // OK
    public Map<String, Object> memberDelete(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);
        String content = (String) resultMap.get("content");
        memberService.delete(userPk, content);
        return map;
    }

    @PatchMapping("/member/categoryUpdate") // OK
    public Map<String, Object> memberCategoryUpdate(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        List<Boolean> list = (List<Boolean>) resultMap.get("cate");
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        memberService.updateCate(userPk, list);
        map.put("status", HttpStatus.OK);

        return map;
    }

    @GetMapping("/member/bookmark")
    public Map<String, Object> memberBookmark(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        if (id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            map = memberService.bookmark(id);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @DeleteMapping("/member/deleteBookmark")
    public Map<String, Object> memberDeleteBookmark(HttpServletRequest request, @RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        String news_id = (String) resultMap.get("news_id");
        if (id == null || news_id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            memberService.deleteBookmark(id, news_id);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }
}
