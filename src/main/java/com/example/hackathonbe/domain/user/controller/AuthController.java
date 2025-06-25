package com.example.hackathonbe.domain.user.controller;

import com.example.hackathonbe.domain.user.dto.UserReqDTO;
import com.example.hackathonbe.domain.user.service.command.UserCommandService;
import com.example.hackathonbe.global.apiPayload.CustomResponse;
import com.example.hackathonbe.global.auth.dto.TokenDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {

    private final UserCommandService userCommandService;

    // 회원가입
    @PostMapping("/signup")
    public CustomResponse<Void> signUp(
            @RequestBody UserReqDTO.SignUp dto
    ) {
        userCommandService.signUp(dto);
        return CustomResponse.created(null);
    }

    // 로그인: 필터에서 처리
    @PostMapping("/login")
    public CustomResponse<TokenDTO> login(
            @RequestBody UserReqDTO.Login dto
    ) {
        return CustomResponse.ok(null);
    }

    // 로그아웃: 필터에서 처리
    @PostMapping("/logout")
    public CustomResponse<Void> logout() {
        return CustomResponse.ok(null);
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public CustomResponse<TokenDTO> refresh() {
        return CustomResponse.ok(null);
    }
}
