package com.keji.green.lit.engine.dto.response;

import com.keji.green.lit.engine.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户信息响应DTO
 * 用于向客户端返回用户信息，不包含敏感信息如密码
 * @author xiangjun_lee
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
     * 用户名
     */
    private String nickName;
    
    /**
     * 用户手机号
     */
    private String phone;
    
    /**
     * 账号创建时间
     */
    private Date gmtCreate;

    /**
     * 简历文本内容
     */
    private String resumeText;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请人数
     */
    private String inviteeCount;
    
    /**
     * 从User实体创建UserResponse的工厂方法
     */
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .uid(user.getUid())
                .nickName(user.getNickName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .resumeText(user.getResumeText())
                .gmtCreate(user.getGmtCreate())
                .inviteCode(user.getInviteCode())
                .build();
    }
} 