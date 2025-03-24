package com.keji.green.lit.engine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册请求DTO
 * 用于接收用户注册的请求数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * 用户手机号
     * 不能为空，且必须符合中国大陆手机号格式
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 短信验证码
     * 用于验证手机号真实性
     */
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;
    
    /**
     * 用户密码
     * 长度6-20位
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6-20位之间")
    private String password;
} 