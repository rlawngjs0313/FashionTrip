package com.example.hackathonbe.global.auth.filter;

import com.example.hackathonbe.domain.user.dto.UserReqDTO;
import com.example.hackathonbe.global.apiPayload.excpetion.code.GeneralErrorCode;
import com.example.hackathonbe.global.apiPayload.excpetion.code.GeneralSuccessCode;
import com.example.hackathonbe.global.auth.converter.TokenConverter;
import com.example.hackathonbe.global.auth.dto.TokenDTO;
import com.example.hackathonbe.global.auth.exception.code.SecurityErrorCode;
import com.example.hackathonbe.global.auth.userDetail.CustomUserDetails;
import com.example.hackathonbe.global.auth.util.HttpResponseUtil;
import com.example.hackathonbe.global.auth.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Validator validator;

    // 로그인 시도
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationServiceException{
        ObjectMapper objectMapper = new ObjectMapper();
        UserReqDTO.Login dto;
        // request -> DTO 매핑
        try {
            dto = objectMapper.readValue(request.getInputStream(), UserReqDTO.Login.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
        // DTO 검증
        Set<ConstraintViolation<UserReqDTO.Login>> violations = validator.validate(dto);
        // 올바르지 않은 경우
        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                    .map(violation -> {
                        String field = violation.getPropertyPath().toString();
                        String message = violation.getMessage();
                        return Map.entry(field, message);
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            try {
                HttpResponseUtil.setErrorResponse(response, GeneralErrorCode.BAD_REQUEST_400, errors);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 인증을 위한 객체 생성
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password());
        // 인증
        return authenticationManager.authenticate(auth);
    }

    // 성공했을 경우
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) throws IOException {

        // 인증 객체 가져오기
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        // 토큰 발행하기
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        // refresh 토큰 유효기간 설정
        Date TokenExpiration = jwtUtil.getRefreshExpiration(refreshToken);
        long exp = TokenExpiration.getTime() - Instant.now().toEpochMilli();
        int maxAge = exp > 0 ? Math.toIntExact(exp / 1000) : 0;

        // refresh 토큰 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAge);
        // HTTPS 사용하면 허용
//        refreshTokenCookie.setSecure(true);

        // refresh 토큰 쿠키에 담기
        response.addCookie(refreshTokenCookie);

        // 응답하기
        TokenDTO tokenDTO = TokenConverter.toTokenDTO(accessToken);
        HttpResponseUtil.setSuccessResponse(response, GeneralSuccessCode.OK, tokenDTO);
    }

    // 실패했을 경우
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException {
        SecurityErrorCode code = getErrorCode(failed);
        HttpResponseUtil.setErrorResponse(response, code, null);
    }

    // 실패 코드 추출
    private SecurityErrorCode getErrorCode(AuthenticationException failed) {
        // 유저가 존재하지 않거나 비밀번호가 틀린 경우
        if (failed instanceof BadCredentialsException) {
            return SecurityErrorCode.BAD_CREDENTIALS;
        // 계정이 정지된 경우
        } else if (failed instanceof LockedException || failed instanceof DisabledException) {
            return SecurityErrorCode.FORBIDDEN;
        // 그 밖에 인증하면서 발생한 모든 예외
        } else if (failed instanceof AuthenticationServiceException) {
            return SecurityErrorCode.INTERNAL_SECURITY_ERROR;
        // 다른 모든 예외
        } else {
            return SecurityErrorCode.UNAUTHORIZED;
        }
    }
}
