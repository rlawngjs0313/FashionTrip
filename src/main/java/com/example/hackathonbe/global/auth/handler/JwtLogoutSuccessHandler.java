package com.example.hackathonbe.global.auth.handler;

import com.example.hackathonbe.global.apiPayload.excpetion.code.BaseCode;
import com.example.hackathonbe.global.apiPayload.excpetion.code.GeneralSuccessCode;
import com.example.hackathonbe.global.util.HttpResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        BaseCode code = GeneralSuccessCode.OK;
        HttpResponseUtil.setSuccessResponse(response, code, null);
    }
}