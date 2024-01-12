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

    @GetMapping("/member/mypage")
    public Map<String, Object> memberMypage(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        String email = (String) session.getAttribute("email");
        String nickname = (String) session.getAttribute("nickname");

        map.put("id", id);
        map.put("email", email);
        map.put("nickname", nickname);
        if (id == null || email == null || nickname == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @PatchMapping("/member/mypage/updatePassword")
    public Map<String, Object> memberMypageUpdatePassword(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        if (id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("flag", false);
        } else {
            if (memberService.updatePw(id, resultMap.get("origin_pw").toString(), resultMap.get("new_pw").toString())) {
                map.put("status", HttpStatus.OK);
                map.put("flag", true);

            } else {
                map.put("status", HttpStatus.BAD_REQUEST);
                map.put("flag", false);
            }
        }
        return map;
    }

    @GetMapping("/member/category")
    public Map<String, Object> memberCategory(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        List<String> category = (List<String>) session.getAttribute("category");
        map.put("categorylist", category);
        map.put("status", HttpStatus.OK);
        return map;
    }

    @PatchMapping("/member/updateNickname")
    public Map<String, Object> memberUpdateNickname(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        String nickname = (String) resultMap.get("nickname");

        if (memberService.updateNickname(id, nickname)) {
            session.removeAttribute("nickname");
            session.setAttribute("nickname", nickname);
            map.put("status", HttpStatus.OK);
            map.put("message", "");
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "닉네임이 중복됩니다.");
        }
        return map;
    }

    @DeleteMapping("/member/delete")
    public Map<String, Object> memberDelete(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        String content = (String) resultMap.get("content");
        if (content == null || id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            session.invalidate();
            memberService.delete(id, content);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @PatchMapping("/member/categoryUpdate")
    public Map<String, Object> memberCategoryUpdate(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        List<Boolean> list = (List<Boolean>) resultMap.get("cate");
        HttpSession session = request.getSession();
        String id = (String) session.getAttribute("id");
        if (id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            memberService.updateCate(id, list);
            session.removeAttribute("cate");
            session.setAttribute("cate", list);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @GetMapping("/member/bookmark")
    public Map<String, Object> memberBookmark(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        if (id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            map = memberService.bookmark(id);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @DeleteMapping("/member/deleteBookmark")
    public Map<String, Object> memberDeleteBookmark(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
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
