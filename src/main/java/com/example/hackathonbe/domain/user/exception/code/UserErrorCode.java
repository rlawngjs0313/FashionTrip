package com.example.hackathonbe.domain.user.exception.code;

import com.example.hackathonbe.global.apiPayload.excpetion.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND,
            "USER404",
            "해당 유저를 찾을 수 없습니다."),
    ALREADY_EXISTS(HttpStatus.BAD_REQUEST,
            "USER400_0",
            "이미 존재하는 유저입니다."),
    INVALID_USER(HttpStatus.BAD_REQUEST,
            "USER400_1",
            "이메일 혹은 비밀번호가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
