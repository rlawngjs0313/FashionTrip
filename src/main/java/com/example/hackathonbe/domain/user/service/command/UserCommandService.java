package com.example.hackathonbe.domain.user.service.command;

import com.example.hackathonbe.domain.user.converter.UserConverter;
import com.example.hackathonbe.domain.user.dto.UserReqDTO;
import com.example.hackathonbe.domain.user.entity.User;
import com.example.hackathonbe.domain.user.exception.UserException;
import com.example.hackathonbe.domain.user.exception.code.UserErrorCode;
import com.example.hackathonbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signUp(
            UserReqDTO.SignUp dto
    ){
        // 기회원인지 확인
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserException(UserErrorCode.ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.password());
        User user = UserConverter.toUser(dto, encodedPassword);
        userRepository.save(user);
    }
}
