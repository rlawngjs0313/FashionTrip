package com.example.hackathonbe.global.auth.converter;

import com.example.hackathonbe.global.auth.dto.TokenDTO;

public class TokenConverter {

    public static TokenDTO toTokenDTO(String accessToken){
        return TokenDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}
