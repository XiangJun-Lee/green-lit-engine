package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.enums.VerificationCodeScene;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.service.RateLimitService;
import com.keji.green.lit.engine.service.VerificationCodeService;
import com.keji.green.lit.engine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Random;

import static com.keji.green.lit.engine.exception.ErrorCode.*;
import static com.keji.green.lit.engine.utils.Constants.*;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

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

    private static final String VERIFICATION_CODE_KEY_PREFIX = "VERIFICATION_CODE:%s:%s";
    private static final String VERIFICATION_CODE_LIMIT_KEY_PREFIX = "VERIFICATION_CODE_LIMIT:%s:%s";

    private final Random random = new Random();

    @Value("${sms.verification.expiration:300}")
    private int verificationCodeExpiration;

    @Resource
    private RateLimitService rateLimitService;

    /**
     * 生成并发送验证码
     * 生成6位随机数字验证码并通过短信发送给用户
     *
     * @param phone 手机号
     * @param scene 验证码场景
     * @return 生成的验证码
     */
    @Override
    public String generateAndSendCode(String phone, VerificationCodeScene scene) {

        // 检查每分钟发送次数限制
        String limitKey = String.format(VERIFICATION_CODE_LIMIT_KEY_PREFIX, scene.getCode(), phone);
        if (rateLimitService.isRateLimited(limitKey, INTEGER_ONE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(), "验证码发送过于频繁，请稍后再试");
        }

        // 生成并存储验证码
        String code = String.format("%06d", random.nextInt(1000000));
        String key = String.format(VERIFICATION_CODE_KEY_PREFIX, scene.getCode(), phone);
        redisUtils.set(key, code, verificationCodeExpiration);
        log.debug("Stored verification code for phone number: {}, scene: {}, code: {}", phone, scene.getCode(), code);
        return code;
    }

    /**
     * 验证验证码
     * 验证用户输入的验证码是否与系统生成的一致
     *
     * @param phone 手机号
     * @param code  验证码
     * @param scene 验证码场景
     * @return 是否有效，true表示验证通过
     */
    @Override
    public boolean verifyCode(String phone, String code, VerificationCodeScene scene) {
        String key = String.format(VERIFICATION_CODE_KEY_PREFIX, scene.getCode(), phone);
        String storedCode = redisUtils.get(key);
        if (StringUtils.isBlank(storedCode)) {
            throw new BusinessException(VERIFICATION_CODE_EXPIRED);
        }
        if (storedCode.equals(code)) {
            // 验证成功后删除验证码
            redisUtils.del(key);
            return true;
        }
        return false;
    }
} 