package com.example.hackathonbe.domain.user.exception;

import com.example.hackathonbe.global.apiPayload.excpetion.CustomException;
import com.example.hackathonbe.global.apiPayload.excpetion.code.BaseErrorCode;

public class UserException extends CustomException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}
