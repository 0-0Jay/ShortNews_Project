package com.ShortNews.ShortNews.social;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
public class KakaoLoginController {

    @Autowired
    private OauthService oauthService;

    @GetMapping("/login/oauth2/code/kakao")
    public String code(@RequestParam String code) {
        return code;
    }

    @GetMapping("/kakao/login/{code}")
    public Map<String, Object> kakaoCallback(@PathVariable("code") String code, HttpServletResponse response) {
        String kakao_access_token = oauthService.getKakaoAccessToken(code);
        Map<String, Object> map = oauthService.createKakaoUser(kakao_access_token);
        boolean message = (boolean) map.get("message");
        if (!message) {
            return map;
        } else {
            Cookie cookie = new Cookie("access_token", map.get("access_token").toString());
            cookie.setMaxAge(60 * 30);
//            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return map;
        }
    }

    @PostMapping("/kakao/appLogin")
    public Map<String, Object> kakaoAppLogin(@RequestBody Map<String, Object> resultMap) throws NoSuchAlgorithmException {
        String id = (String) resultMap.get("id");
        String email = (String) resultMap.get("email");
        Map<String, Object> map = oauthService.oauthAppLogin(id, email, "K");
        map.put("status", HttpStatus.OK);
        return map;
    }

}
