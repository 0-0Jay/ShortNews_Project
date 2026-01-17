package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.JavaCode;
import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.Token.TokenService;
import com.ShortNews.ShortNews.dto.SessionDto;
import com.ShortNews.ShortNews.service.LoginService;
import com.ShortNews.ShortNews.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private JavaCode javaCode;
    @Autowired
    private NotificationController notificationController;

    @PostMapping("/login/idCheck") // OK
    public Map<String, Object> loginIdCheck(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String id = resultMap.get("id").toString();
        map.put("id", id);

        if (loginService.idCheck(id)) {
            map.put("flag", true);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("flag", false);
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PostMapping("/login/password") // OK
    public Map<String, Object> loginPassword(@RequestBody Map<String, Object> resultMap, HttpServletResponse response) throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        String id = resultMap.get("id").toString();
        String pw = resultMap.get("pw").toString();
        SessionDto sessionDto = loginService.loginCheck(id, pw);
        if (sessionDto.getId() != null) {
            String token = jwtTokenProvider.createToken(id);
            String refresh_token = jwtTokenProvider.createRefreshToken(id);
            tokenService.saveRefreshToken(id, refresh_token);

            Cookie cookie = new Cookie("access_token", token);
            cookie.setMaxAge(60 * 30);
//            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            map.put("dto", sessionDto);
//            map.put("alarm", notificationService.getAlarm(id));
            map.put("access_token", token);
            map.put("refresh_token", refresh_token);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }

    @PostMapping("/login/findPassword") // OK
    public Map<String, Object> loginFindPassword(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String id = resultMap.get("id").toString();
        String phone = resultMap.get("phone").toString();
        String code = null;
        if (loginService.findPassword(id, phone)) {
            code = javaCode.makeCode();
            javaCode.sendSMS(phone, code);
            map.put("flag", true);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("flag", false);
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        map.put("code", code);
        return map;
    }

    @PostMapping("/login/findId")
    public Map<String, Object> loginFindId(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String code = null;
        String phone = resultMap.get("phone").toString();
        String id = loginService.findId(phone);
        if (id != null) {
            code = javaCode.makeCode();
            javaCode.sendSMS(phone, code);
            map.put("flag", true);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("flag", false);
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        map.put("code", code);
        map.put("id", id);
        return map;
    }

    @PatchMapping("/login/updatePassword")
    public Map<String, Object> loginUpdatePassword(@RequestBody Map<String, Object> resultMap, HttpServletRequest request) throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        String pw = resultMap.get("pw").toString();
        String id = resultMap.get("id").toString();
        loginService.updatePassword(id, pw);
        map.put("status", HttpStatus.OK);
        return map;
    }
}