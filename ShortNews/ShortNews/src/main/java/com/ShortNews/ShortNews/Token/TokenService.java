package com.ShortNews.ShortNews.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    public boolean accessCheck(String access_token) {
        if (jwtTokenProvider.validateToken(access_token)) {
            return true;
        } else {
            //refresh 보내줘
            return false;
        }
    }
    public boolean refreshCheck(String refresh_token) {
        Optional<Refreshtoken> refreshToken = refreshTokenRepository.findByRefreshtoken(refresh_token);
        if (!refresh_token.isEmpty() && jwtTokenProvider.validateToken(refresh_token)) {
            // access 재발급
            return true;
        }else {
            //다시 로그인 해
            return false;
        }
    }

    public void saveRefreshToken(String id, String token) {
        Optional<Refreshtoken> list = refreshTokenRepository.findById(id);
        if (list.isEmpty()) {
            Refreshtoken refreshToken = Refreshtoken.builder()
                    .id(id)
                    .refreshtoken(token)
                    .build();
            refreshTokenRepository.save(refreshToken);
        } else {
            refreshTokenRepository.update(id, token);
        }
    }
}
