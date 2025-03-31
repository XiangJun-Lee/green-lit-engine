package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.port.SmsPort;
import com.keji.green.lit.engine.service.SmsService;
import com.keji.green.lit.engine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 短信服务实现类
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Resource
    private SmsPort smsPort;

    @Resource
    private RedisUtils redisUtils;

    @Value("${sms.code.expire-minutes:5}")
    private int codeExpireMinutes;

    private static final String SMS_CODE_KEY_PREFIX = "sms:code:";
    private static final String SMS_CODE_COUNT_KEY_PREFIX = "sms:code:count:";

    @Override
    public boolean sendVerificationCode(String phone) {
        // 生成验证码
        String code = smsPort.generateVerificationCode();
        
        // 发送验证码
        boolean success = smsPort.sendVerificationCode(phone, code);
        if (!success) {
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "发送验证码失败");
        }
        
        // 保存验证码到Redis，设置过期时间
        String codeKey = SMS_CODE_KEY_PREFIX + phone;
        redisUtils.set(codeKey, code, codeExpireMinutes * 60);
        
        // 记录发送次数
        String countKey = SMS_CODE_COUNT_KEY_PREFIX + phone;
        redisUtils.incr(countKey);
        redisUtils.expire(countKey, 24 * 60 * 60); // 24小时过期
        
        return true;
    }
} 