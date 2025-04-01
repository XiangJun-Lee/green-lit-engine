package com.keji.green.lit.engine.dto;

import com.keji.green.lit.engine.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private Long uid;
    
    /**
     * 用户手机号
     */
    private String phone;
    
    /**
     * 账号创建时间
     */
    private Date gmtCreate;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    
    /**
     * 积分余额
     */
    private Integer pointsBalance;
    
    /**
     * 简历文本内容
     */
    private String resumeText;
    
    /**
     * 客户端连接信息
     */
    private String clientConnection;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 从User实体创建UserResponse的工厂方法
     */
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .uid(user.getUid())
                .phone(user.getPhone())
                .gmtCreate(user.getGmtCreate())
                .pointsBalance(user.getPointsBalance())
                .resumeText(user.getResumeText())
                .build();
    }
} 