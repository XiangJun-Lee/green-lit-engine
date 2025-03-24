package com.keji.green.lit.engine.service;

/**
 * 验证码服务接口
 * 提供验证码生成、发送和验证的功能
 */
public interface VerificationCodeService {
    
    /**
     * 生成并发送验证码
     * 生成6位随机数字验证码并通过短信发送给用户
     *
     * @param phone 手机号
     * @return 生成的验证码
     */
    String generateAndSendCode(String phone);
    
    /**
     * 验证验证码
     * 验证用户输入的验证码是否与系统生成的一致
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否有效，true表示验证通过
     */
    boolean verifyCode(String phone, String code);
} 