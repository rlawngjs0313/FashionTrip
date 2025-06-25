package com.example.hackathonbe.global.auth.userDetail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {

    private Long socialUid;
    private String email;
    private String password;
}
