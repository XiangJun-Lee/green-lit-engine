package com.keji.green.lit.engine.dto;

import com.keji.green.lit.engine.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 * 用于向客户端返回用户信息，不包含敏感信息如密码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户手机号
     */
    private String phone;
    
    /**
     * 账号创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;
    
    /**
     * 积分余额
     */
    private Integer creditBalance;
    
    /**
     * 简历文本内容
     */
    private String resumeText;
    
    /**
     * 从User实体创建UserResponse的工厂方法
     * 
     * @param user 用户实体
     * @return UserResponse实例
     */
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .creditBalance(user.getCreditBalance())
                .resumeText(user.getResumeText())
                .build();
    }
} 