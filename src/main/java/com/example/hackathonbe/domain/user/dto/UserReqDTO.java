package com.example.hackathonbe.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserReqDTO {

    // 회원가입
    public record SignUp(
            String email,
            String password,
            String nickname
    ){}

    // 로그인
    public record Login(
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,
            @NotBlank(message = "비밀번호를 입력해주세요.")
            String password
    ){}
}
