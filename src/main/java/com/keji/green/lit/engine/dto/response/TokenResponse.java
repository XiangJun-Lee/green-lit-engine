package com.keji.green.lit.engine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token响应对象
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    
    /**
     * JWT令牌
     */
    private String token;
    
    /**
     * 令牌类型，固定值为"Bearer"
     */
    private String type;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户手机号
     */
    private String phone;
    
    /**
     * 创建TokenResponse实例的工厂方法
     * 
     * @param token JWT令牌
     * @param uid 用户ID
     * @param phone 用户手机号
     * @return TokenResponse实例
     */
    public static TokenResponse of(String token, Long uid, String phone) {
        return TokenResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(uid)
                .phone(phone)
                .build();
    }
} 