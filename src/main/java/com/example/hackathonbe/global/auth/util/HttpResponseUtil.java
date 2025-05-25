package com.example.hackathonbe.global.auth.util;

import com.example.hackathonbe.global.apiPayload.CustomResponse;
import com.example.hackathonbe.global.apiPayload.excpetion.code.BaseCode;
import com.example.hackathonbe.global.apiPayload.excpetion.code.BaseErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void setSuccessResponse(
            HttpServletResponse response,
            BaseCode httpStatus,
            Object body
    ) throws IOException {
        log.info("[*] Success Response");
        CustomResponse<Object> responseBody = CustomResponse.onSuccess(httpStatus, body);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), responseBody);
    }

    public static void setErrorResponse(
            HttpServletResponse response,
            BaseErrorCode httpStatus,
            Object body
    ) throws IOException {

        log.info("[*] Failure Response");
        CustomResponse<Object> responseBody = CustomResponse.onFailure(httpStatus, body);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), responseBody);
    }
}
