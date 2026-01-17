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


    @PatchMapping("/member/updateNickname") // OK
    public Map<String, Object> memberUpdateNickname(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        String nickname = (String) resultMap.get("nickname");
        if (memberService.updateNickname(userPk, nickname)) {
            map.put("status", HttpStatus.OK);
            map.put("message", "");
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("message", "닉네임이 중복됩니다.");
        }
        return map;
    }

    @PatchMapping("/member/delete") // OK
    public Map<String, Object> memberDelete(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        String content = (String) resultMap.get("content");
        memberService.delete(userPk, content);
        map.put("status", HttpStatus.OK);
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
        String token, userPk;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        userPk = jwtTokenProvider.getUserPk(token);

        if (userPk == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            map.put("bookmark", memberService.bookmark(userPk));
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @DeleteMapping("/member/deleteBookmark")
    public Map<String, Object> memberDeleteBookmark(HttpServletRequest request, @RequestBody Map<String, Object> resultMap) {
        String token, id;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        id = jwtTokenProvider.getUserPk(token);

        String news_id = (String) resultMap.get("news_id");
        if (id == null || news_id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            memberService.deleteBookmark(id, news_id);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @DeleteMapping("/member/deleteAllBookmark")
    public Map<String, Object> memberDeleteAllBookmark(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        System.out.println(id);
        if (id == null) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            memberService.deleteAllBookmark(id);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @PatchMapping("/member/tts")
    public Map<String, Object> memberTts(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        String token, id;
        Map<String, Object> map = new HashMap<>();
        token = jwtTokenProvider.resolveToken(request);
        id = jwtTokenProvider.getUserPk(token);
        String model = (String)resultMap.get("model");
        String speed = (String)resultMap.get("speed");
        if (model != null && speed != null) {
            memberService.updateMemberTts(id, model, speed);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PostMapping("/member/mypage/checkPhone")
    public Map<String, Object> checkPhone(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String phone = (String) resultMap.get("phone");
        String code = memberService.checkPhone(phone);
        if (code.isEmpty()) {
            map.put("status", HttpStatus.BAD_REQUEST);
        } else {
            map.put("code", code);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @PatchMapping("/member/mypage/updatePhone")
    public Map<String, Object> updatePhone(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        String token = jwtTokenProvider.resolveToken(request);
        String id = jwtTokenProvider.getUserPk(token);
        String phone = (String) resultMap.get("phone");
        memberService.updatePhone(id, phone);
        map.put("status", HttpStatus.OK);
        return map;
    }
}
