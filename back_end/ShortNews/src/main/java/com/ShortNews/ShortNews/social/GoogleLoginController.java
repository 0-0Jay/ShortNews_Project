package com.ShortNews.ShortNews.social;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class GoogleLoginController {

    @Autowired
    private OauthService oauthService;
    @PostMapping("/login/oauth2/code/google")
    public String loginUrlGoogle() { return oauthService.googleLoginUrl(); }

    @GetMapping("/login/oauth2/code/google")
    public Map<String, Object> loginGoogle(@RequestParam(value = "code") String authCode, HttpServletResponse response) throws NoSuchAlgorithmException {
        String google_access_token = oauthService.getGoogleAccessToken(authCode);
        // 여기 로직 추가 해야 함
        Map<String, Object> map = oauthService.googleInfo(google_access_token);
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
}
