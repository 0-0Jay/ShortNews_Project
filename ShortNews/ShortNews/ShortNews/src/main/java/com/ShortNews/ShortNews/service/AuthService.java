package com.ShortNews.ShortNews.service;

import com.ShortNews.ShortNews.Token.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    public void logout(String id) {
        refreshTokenRepository.deleteById(id);
    }
}
