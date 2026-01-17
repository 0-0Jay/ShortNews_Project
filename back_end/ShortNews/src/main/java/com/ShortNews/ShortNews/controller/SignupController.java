package com.ShortNews.ShortNews.controller;


import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.Token.TokenService;
import com.ShortNews.ShortNews.dto.MemberDto;
import com.ShortNews.ShortNews.dto.SessionDto;
import com.ShortNews.ShortNews.entity.Member_tts;
import com.ShortNews.ShortNews.service.LoginService;
import com.ShortNews.ShortNews.service.NotificationService;
import com.ShortNews.ShortNews.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@RestController
public class SignupController {

    @Autowired
    private SignupService signupService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/signup/idCheck")
    public Map<String, Object> signupIdCheck(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String id = resultMap.get("id").toString();

        if (signupService.idCheck(id)) {
            map.put("flag", true);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("flag", false);
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PostMapping("/signup/phoneCheck")
    public Map<String, Object> signupPhoneCheck(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String phone = resultMap.get("phone").toString();
        String code = signupService.phoneCheck(phone);

        if (code.equals("")) {
            map.put("flag", false);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("code", null);
        } else {
            map.put("flag", true);
            map.put("code", code);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }

    @PostMapping("/signup/submit")
    public Map<String, Object> signupSubmit(@RequestBody MemberDto memberDto, HttpServletRequest request) throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        if (!signupService.nicknameCheck(memberDto.getNickname())) {
            map.put("message", "닉네임이 중복되었습니다.");
            map.put("flag", false);
            map.put("status", HttpStatus.BAD_REQUEST);
            map.put("dto", memberDto);
        } else {
            map.put("message", "성공");
            map.put("flag", true);
            map.put("status", HttpStatus.OK);
        }
        return map;
    }
    @PostMapping("/signup/selectCategory")
    public Map<String, Object> signupSelectCategory(@RequestBody Map<String, Object> resultMap, HttpServletResponse response) throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        List<Boolean> cate = (List<Boolean>) resultMap.get("cate");
        String id = (String) resultMap.get("id");
        String pw = (String) resultMap.get("pw");
        String nickname = (String)resultMap.get("nickname");
        nickname = (nickname.isEmpty()) ? id : nickname;
        String platform = (String) resultMap.get("platform");
        String phone = (String) resultMap.get("phone");
        if (!platform.equals("I")) {
            phone += platform;
        }

        MemberDto memberDto = MemberDto.builder()
                        .id(id)
                        .pw(pw)
                        .phone(phone)
                        .nickname(nickname)
                        .build();
        signupService.join(memberDto, platform);

        List<String> list = signupService.selectCategory(cate, id);
        SessionDto sessionDto = SessionDto.builder()
                .id(id)
                .nickname(nickname)
                .category(list)
                .model("_male")
                .platform(platform)
                .speed("1")
                .phone(phone)
                .alarm(1)
                .build();

        String token = jwtTokenProvider.createToken(id);
        String refresh_token = jwtTokenProvider.createRefreshToken(id);
        tokenService.saveRefreshToken(id, refresh_token);

        Cookie cookie = new Cookie("access_token", token);
        cookie.setMaxAge(60 * 30);
//        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        map.put("dto", sessionDto);
//        map.put("alarm", notificationService.getAlarm(id));
        map.put("access_token", token);
        map.put("refresh_token", refresh_token);
        map.put("status", HttpStatus.OK);
        return map;
    }
}