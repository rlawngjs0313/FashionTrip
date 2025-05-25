package com.example.hackathonbe.global.auth.exception;

import com.example.hackathonbe.global.auth.exception.code.SecurityErrorCode;
import com.example.hackathonbe.global.auth.util.HttpResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        SecurityErrorCode code = SecurityErrorCode.FORBIDDEN;
        HttpResponseUtil.setErrorResponse(response, code, null);
    }
}
