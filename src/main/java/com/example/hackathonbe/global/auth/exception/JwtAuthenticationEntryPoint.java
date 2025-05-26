package com.example.hackathonbe.global.auth.exception;

import com.example.hackathonbe.global.auth.exception.code.SecurityErrorCode;
import com.example.hackathonbe.global.util.HttpResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        SecurityErrorCode code = SecurityErrorCode.UNAUTHORIZED;
        HttpResponseUtil.setErrorResponse(response, code, null);
    }
}
