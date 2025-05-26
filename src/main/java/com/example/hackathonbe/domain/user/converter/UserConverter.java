package com.example.hackathonbe.domain.user.converter;

import com.example.hackathonbe.domain.user.dto.UserReqDTO;
import com.example.hackathonbe.domain.user.entity.User;

public class UserConverter {

    // 회원가입 : DTO, password -> Entity
    public static User toUser(
            UserReqDTO.SignUp dto,
            String password
    ){
        return User.builder()
                .email(dto.email())
                .password(password)
                .nickname(dto.nickname())
                .build();
    }
}
