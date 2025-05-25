package com.example.hackathonbe.global.auth.dto;

import lombok.Builder;

@Builder
public record TokenDTO(
        String accessToken
){}
