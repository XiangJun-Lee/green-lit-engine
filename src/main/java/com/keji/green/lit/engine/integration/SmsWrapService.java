package com.keji.green.lit.engine.integration;

/**
 * 短信服务防腐层接口
 * 用于隔离外部短信平台的具体实现
 */
public interface SmsWrapService {
    
    /**
     * 发送验证码短信
     *
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phone, String code);
    
    /**
     * 生成验证码
     *
     * @return 6位数字验证码
     */
    String generateVerificationCode();
} 