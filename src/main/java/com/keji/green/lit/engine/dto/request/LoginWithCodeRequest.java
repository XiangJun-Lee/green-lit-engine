package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码登录请求request
 * 用于接收用户密码登录的请求数据
 * @author xiangjun_lee
 * @date 2025/4/5 16:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginWithCodeRequest {

    /**
     * 用户手机号
     * 不能为空，用作登录账号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 验证码
     * 不能为空，用于验证用户身份
     */
    @NotBlank(message = "验证码不能为空")
    private String code;
}
