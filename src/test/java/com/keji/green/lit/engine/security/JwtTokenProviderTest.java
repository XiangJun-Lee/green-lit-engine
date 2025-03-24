package com.keji.green.lit.engine.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String testPhone = "13800138000";
    private final String testSecret = "bXktc3VwZXItc2VjcmV0LWtleS1mb3ItZ3JlZW4tbGl0LWVuZ2luZS1wcm9qZWN0LTIwMjQtdmVyeS1zZWN1cmUtdGVzdC1rZXktbG9uZy1lbm91Z2gtZm9yLWhzNTEyLWFsZ29yaXRobQ==";
    private final long testValidityInMs = 3600000; // 1小时

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(testSecret, testValidityInMs);
    }

    @Test
    void createToken_shouldCreateValidToken() {
        // 准备
        UserDetails userDetails = new User(
                testPhone,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // 执行
        String token = jwtTokenProvider.createToken(authentication);

        // 验证
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        
        // 获取认证信息，检查用户名
        Authentication resultAuth = jwtTokenProvider.getAuthentication(token);
        assertEquals(testPhone, resultAuth.getName());
    }

    @Test
    void getAuthentication_shouldReturnCorrectUsername() {
        // 准备
        UserDetails userDetails = new User(
                testPhone,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String token = jwtTokenProvider.createToken(authentication);

        // 执行
        Authentication resultAuth = jwtTokenProvider.getAuthentication(token);

        // 验证
        assertEquals(testPhone, resultAuth.getName());
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        // 准备
        UserDetails userDetails = new User(
                testPhone,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String token = jwtTokenProvider.createToken(authentication);

        // 执行和验证
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        // 准备
        String invalidToken = "invalid.token.string";

        // 执行和验证
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        // 准备
        // 创建一个有效期非常短的JwtTokenProvider实例
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(testSecret, 1); // 1毫秒有效期
        
        UserDetails userDetails = new User(
                testPhone,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        
        // 创建短期令牌
        String token = shortLivedProvider.createToken(authentication);
        
        // 等待令牌过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 执行和验证
        assertFalse(shortLivedProvider.validateToken(token));
    }

    @Test
    void getAuthentication_shouldReturnValidAuthentication() {
        // 准备
        UserDetails userDetails = new User(
                testPhone,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication originalAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String token = jwtTokenProvider.createToken(originalAuth);

        // 执行
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // 验证
        assertNotNull(authentication);
        assertEquals(testPhone, authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
} 