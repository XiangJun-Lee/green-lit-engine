package com.keji.green.lit.engine.service;

/**
 * 短信服务接口
 */
public interface SmsService {
    
    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone);
} 