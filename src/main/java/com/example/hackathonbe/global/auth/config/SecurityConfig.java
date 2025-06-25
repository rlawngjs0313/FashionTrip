package com.example.hackathonbe.global.auth.config;

import com.example.hackathonbe.global.auth.exception.JwtAccessDeniedHandler;
import com.example.hackathonbe.global.auth.exception.JwtAuthenticationEntryPoint;
import com.example.hackathonbe.global.auth.filter.JwtAuthFilter;
import com.example.hackathonbe.global.auth.filter.JwtLoginFilter;
import com.example.hackathonbe.global.auth.handler.JwtLogoutHandler;
import com.example.hackathonbe.global.auth.handler.JwtLogoutSuccessHandler;
import com.example.hackathonbe.global.auth.userDetail.CustomUserDetailsService;
import com.example.hackathonbe.global.auth.util.JwtUtil;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint customEntryPoint;
    private final JwtAccessDeniedHandler customAccessDeniedHandler;
    private final AuthenticationConfiguration authConfig;
    private final Validator validator;

    // 아래 3개는 Swagger에 대한 URL
    private final String[] allowUrl = {
            "/signup",
            "/login",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 어떤 URL에 Security를 걸 것인지 permitAll을 허용, hasRole은 특정 role이 있어야 허용, authenticated는 인증 필요
                .authorizeHttpRequests(request -> request
                        .requestMatchers(allowUrl).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // Http Basic 인증 방식 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // Form Login 인증 방식 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 토큰 검증 필터, 로그인 필터 대체
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                // 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                // 세션 정책 변경: JSESSIONID 사용 X
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 로그인 필터 인스턴스화
        JwtLoginFilter jwtLoginFilter =
                new JwtLoginFilter(authenticationManager(authConfig), jwtUtil, validator);
        // 로그인 필터 추가
        http.addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class);

        // 로그아웃 핸들러 추가
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("refreshToken")
                .addLogoutHandler(jwtLogoutHandler())
                .logoutSuccessHandler(jwtLogoutSuccessHandler())
        );
        return http.build();
    }

    // 토큰 검증 필터
    @Bean
    JwtAuthFilter jwtAuthFilter() {return new JwtAuthFilter(jwtUtil, customUserDetailsService);}

    // 로그아웃 핸들러
    @Bean
    JwtLogoutHandler jwtLogoutHandler() {return new JwtLogoutHandler(jwtUtil);}

    // 로그아웃 성공 핸들러
    @Bean
    JwtLogoutSuccessHandler jwtLogoutSuccessHandler() {return new JwtLogoutSuccessHandler();}

    // AuthenticationProvider에서 사용할 passwordEncoder 설정
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SecurityContextRepository 빈 등록
    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}