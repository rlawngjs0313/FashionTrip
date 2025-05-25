package com.example.hackathonbe.global.auth.userDetail;

import com.example.hackathonbe.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends AuthUser implements UserDetails {

    public CustomUserDetails(User user) {
        super(user.getSocialUid(), user.getEmail(), user.getPassword());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return super.getEmail();
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public Long getSocialUid() {
        return super.getSocialUid();
    }
}
