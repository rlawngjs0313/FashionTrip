package com.example.hackathonbe.global.apiPayload.excpetion;

import com.example.hackathonbe.global.apiPayload.excpetion.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final BaseErrorCode code;

    public CustomException(BaseErrorCode errorCode) {
        this.code = errorCode;
    }
}