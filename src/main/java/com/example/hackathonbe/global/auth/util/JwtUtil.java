package com.example.hackathonbe.global.auth.util;

import com.example.hackathonbe.global.auth.userDetail.CustomUserDetails;
import com.example.hackathonbe.global.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey secretKey; // Key
    private final Duration accessExpiration;
    private final Duration refreshExpiration;
    private final RedisUtil redisUtil;

    // 토큰 접두사: Redis 전용
    private final String BLACK_LIST_PREFIX = "token_blacklist:";

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.time.access-expiration}") long accessExpiration,
            @Value("${spring.jwt.time.refresh-expiration}") long refreshExpiration,
            RedisUtil redisUtil
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
        this.redisUtil = redisUtil;
    }

    // AccessToken 생성
    public String createAccessToken(CustomUserDetails user) {
        return createToken(user, accessExpiration);
    }

    // RefreshToken 생성
    public String createRefreshToken(CustomUserDetails user) {
        return createToken(user, refreshExpiration);
    }

    /** Token 유효기간 가져오기
     *
     * @param token 유효기간 가져올 토큰
     * @return 토큰의 유효기간을 가져옵니다
     */
    public Date getTokenExpiration(String token) {
        try {
            return getClaims(token).getPayload().getExpiration(); // Parsing해서 유효기간 가져오기
        } catch (JwtException e) {
            return null;
        }
    }

    /** 토큰에서 이메일 가져오기
     *
     * @param token 유저 정보를 추출할 토큰
     * @return 유저 이메일을 토큰에서 추출합니다
     */
    public String getEmail(String token) {
        try {
            return getClaims(token).getPayload().getSubject(); // Parsing해서 Subject 가져오기
        } catch (JwtException e) {
            return null;
        }
    }

    /** 토큰 유효성 확인
     *
     * @param token 유효한지 확인할 토큰
     * @return True, False 반환합니다
     */
    public boolean isValid(String token) {
        try {
            // 토큰이 블랙리스트에 존재하는지 확인
            if (isBlackList(token)){
                return false;
            }
            getClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /** 토큰 블랙리스트 설정 (Redis)
     * @param token 블랙리스트에 넣을 토큰 -> token_blacklist : token
     */
    public void setBlackList(String token) {
        // 토큰 유효기간 확인
        Date exp = getTokenExpiration(token);
        // 블랙리스트 유효기간 설정: 현재 시간 - 만료기간 + 1분
        Duration duration = Duration.between(Instant.now(), exp.toInstant())
                .plus(Duration.ofMinutes(1));
        redisUtil.save(BLACK_LIST_PREFIX+token, "", duration);
    }

    /** 토큰 블랙리스트에 존재하는지 확인 (Redis)
     *
     * @param token 존재 여부 확인할 토큰
     * @return True, False를 반환합니다
     */
    public boolean isBlackList(String token) {
        return redisUtil.hasKey(BLACK_LIST_PREFIX+token);
    }

    // 토큰 생성
    private String createToken(CustomUserDetails user, Duration expiration) {
        Instant now = Instant.now();

        // 인가 정보
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getUsername()) // User 이메일을 Subject로
                .claim("role", authorities)
                .claim("socialUid", user.getSocialUid())
                .issuedAt(Date.from(now)) // 언제 발급한지
                .expiration(Date.from(now.plus(expiration))) // 언제까지 유효한지
                .signWith(secretKey) // sign할 Key
                .compact();
    }

    // 토큰 정보 가져오기
    private Jws<Claims> getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);
    }
}