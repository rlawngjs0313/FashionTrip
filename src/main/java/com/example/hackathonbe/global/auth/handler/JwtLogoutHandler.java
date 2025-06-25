package com.example.hackathonbe.global.auth.handler;

import com.example.hackathonbe.global.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        // 토큰 블랙리스트 처리
        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");
        jwtUtil.setBlackList(token);
    }
}
