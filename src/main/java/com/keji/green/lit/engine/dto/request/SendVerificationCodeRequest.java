package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送验证码请求
 * @author xiangjun_lee
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendVerificationCodeRequest {

    /**
     * 用户手机号
     * 不能为空，且必须符合中国大陆手机号格式
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 验证码场景
     * 不能为空，用于区分不同业务场景
     */
    @NotBlank(message = "验证码场景不能为空")
    private String scene;
} 