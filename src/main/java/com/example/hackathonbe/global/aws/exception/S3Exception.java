package com.example.hackathonbe.global.aws.exception;

import com.example.hackathonbe.global.apiPayload.excpetion.CustomException;
import com.example.hackathonbe.global.apiPayload.excpetion.code.BaseErrorCode;

public class S3Exception extends CustomException {
    public S3Exception(BaseErrorCode code) {
        super(code);
    }
}