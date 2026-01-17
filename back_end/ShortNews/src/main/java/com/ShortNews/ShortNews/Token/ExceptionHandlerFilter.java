package com.ShortNews.ShortNews.Token;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    /**
     * 토큰 관련 에러 핸들링
     * JwtTokenFilter 에서 발생하는 에러를 핸들링해준다.
     */

    private final ObjectMapper objectMapper; // Jackson ObjectMapper를 주입받습니다.
    public ExceptionHandlerFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            //토큰의 유효기간 만료
            log.error("만료된 토큰입니다");

            request.setAttribute("exception", "만료");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", true);
            errorResponse.put("message", "만료된 토큰");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        } catch (JwtException | IllegalArgumentException e) {

            //유효하지 않은 토큰
            log.error("유효하지 않은 토큰이 입력되었습니다.");

            request.setAttribute("exception", "유효하지않음");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", false);
            errorResponse.put("message", "유효하지않은 토큰");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

        } catch (NoSuchElementException e) {

            //사용자 찾을 수 없음
            log.error("사용자를 찾을 수 없습니다.");

            request.setAttribute("exception", "사용자 x");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", false);
            errorResponse.put("message", "사용자를 찾을 수 없음");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

}