package com.ShortNews.ShortNews.controller;

import com.ShortNews.ShortNews.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @DeleteMapping("/logout")
    public Map<String, Object> logout(@RequestBody Map<String, Object> resultMap) {
        Map<String, Object> map = new HashMap<>();
        String id = (String)resultMap.get("id");
        if (id != null) {
            authService.logout(id);
            map.put("status", HttpStatus.OK);
        } else {
            map.put("status", HttpStatus.BAD_REQUEST);
        }
        return map;
    }
}
