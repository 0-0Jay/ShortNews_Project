package com.ShortNews.ShortNews.Token;

import com.ShortNews.ShortNews.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.equals("/login/password")) {
            chain.doFilter(request, response);
            return;
        }
        // 헤더에서 JWT와 토큰, 토큰타입.
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        System.out.println(token);
        String type = jwtTokenProvider.getType(token);
        System.out.println(type + "!!!");
        // 유효한 토큰인지 확인합니다.
        // access 토큰이면 유효 한지 확인. 유효하다면 성공, 유효하지 않다면 리프레시 토큰으로 다시 보내라고 요청
        // refresh 토큰이면 유효 한지 확인 있으면 access 재발급, 없다면 로그인 다시 하라고 요청
        if (token != null) {
            if (type.equals("access")) {
                if (token != null && jwtTokenProvider.validateToken(token)) {
                    // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    // SecurityContext 에 Authentication 객체를 저장합니다.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                if (token != null && jwtTokenProvider.validateToken(token)) {
                    String id = jwtTokenProvider.getUserPk(token).toString();
                    System.out.println(id);
                    Optional<Refreshtoken> list = refreshTokenRepository.findByRefreshtoken(token);
                    if (list.get().getRefreshtoken().equals(token)) {
                        jwtTokenProvider.setHeaderAccessToken((HttpServletResponse) response, jwtTokenProvider.createToken(id));
                    } else {
                        authService.logout(id);
                    }
                    // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    System.out.println(authentication);
                    // SecurityContext 에 Authentication 객체를 저장합니다.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            }
        }
        chain.doFilter(request, response);
    }
}