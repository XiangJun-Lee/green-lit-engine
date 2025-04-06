package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.service.VerificationCodeService;
import com.keji.green.lit.engine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Random;

import static com.keji.green.lit.engine.exception.ErrorCode.*;

/**
 * 验证码服务实现类
 * 提供验证码的生成、缓存和验证功能
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {


    @Resource
    private RedisUtils redisUtils;

    private static final String VERIFICATION_CODE_KEY_PREFIX = "verificationCode:%s";

    private final Random random = new Random();

    @Value("${sms.verification.expiration:60}")
    private int verificationCodeExpiration;

    /**
     * 生成并发送验证码
     * 生成6位随机数字验证码并通过短信发送给用户
     *
     * @param phone 手机号
     * @return 生成的验证码
     */
    @Override
    public String generateAndSendCode(String phone) {
        String code = String.format("%06d", random.nextInt(1000000));
        String key = getVerificationCodeKey(phone);
        redisUtils.set(key, code, verificationCodeExpiration);
        log.debug("Stored verification code for phone number: {},code:{}", phone, code);
        return code;
    }

    /**
     * 验证验证码
     * 验证用户输入的验证码是否与系统生成的一致
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否有效，true表示验证通过
     */
    @Override
    public boolean verifyCode(String phone, String code) {
        String key = getVerificationCodeKey(phone);
        String storedCode = redisUtils.get(key);
        if (StringUtils.isBlank(storedCode)){
            throw new BusinessException(VERIFICATION_CODE_EXPIRED);
        }
        if (storedCode.equals(code)) {
            // 验证成功后删除验证码
            redisUtils.del(key);
            return true;
        }
        return false;
    }

    private static String getVerificationCodeKey(String phone) {
        return String.format(VERIFICATION_CODE_KEY_PREFIX, phone);
    }
} 