package com.example.hackathonbe.global.auth.userDetail;

import com.example.hackathonbe.domain.user.entity.User;
import com.example.hackathonbe.domain.user.exception.UserException;
import com.example.hackathonbe.domain.user.exception.code.UserErrorCode;
import com.example.hackathonbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 이메일로 사용자 정보 가져오기
    @Override
    public UserDetails loadUserByUsername(String username) throws UserException {
        User user = userRepository.findUserByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException(UserErrorCode.NOT_FOUND.getMessage()));
        return new CustomUserDetails(user);
    }
}
