package com.example.hackathonbe.domain.user.entity;

import com.example.hackathonbe.domain.user.enums.Role;
import com.example.hackathonbe.domain.user.enums.SocialLogin;
import com.example.hackathonbe.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Random;

@Entity
@Table(name = "user")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    @Size(min = 2, max = 8, message = "닉네임은 2글자 이상 8글자 이하이어야 합니다.")
    @Pattern(regexp = "^[가-힣]*$", message = "완성된 한글만 입력할 수 있습니다.")
    private String nickname;

    @Column(name = "profile_image")
    @Builder.Default
    private String profileImage = "https://hackathon-i-team.s3.ap-northeast-2.amazonaws.com/user/Anonymous_User.jpg";

    @Column(name = "email")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Builder.Default
    private String email = "anonymous@example.com";

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "social_login")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SocialLogin socialLogin = SocialLogin.LOCAL;

    @Column(name = "social_uid")
    @Builder.Default
    private Long socialUid = new Random().nextLong(Long.MAX_VALUE);
}
