package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 密码登录请求request
 * 用于接收用户密码登录的请求数据
 * @author xiangjun_lee
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginWithPasswordRequest {
    
    /**
     * 用户手机号
     * 不能为空，用作登录账号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    /**
     * 用户密码
     * 不能为空，用于验证用户身份
     */
    @NotBlank(message = "密码不能为空")
    private String password;
} 