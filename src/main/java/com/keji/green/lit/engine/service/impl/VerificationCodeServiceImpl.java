package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * 提供验证码的生成、缓存和验证功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    /**
     * 缓存管理器，用于存储验证码
     */
    private final CacheManager cacheManager;
    
    /**
     * 随机数生成器
     */
    private final Random random = new Random();
    
    /**
     * 验证码过期时间（秒）
     */
    @Value("${sms.verification.expiration:90}")
    private int codeExpirationSeconds;
    
    /**
     * 验证码缓存名称
     */
    private static final String VERIFICATION_CODE_CACHE = "verificationCodes";

    /**
     * 生成并发送验证码
     * 生成6位随机数字验证码并存入缓存
     * 实际环境中会调用短信服务发送验证码
     *
     * @param phone 手机号
     * @return 生成的验证码
     */
    @Override
    public String generateAndSendCode(String phone) {
        String code = generateRandomCode();
        
        // 缓存验证码
        Cache cache = cacheManager.getCache(VERIFICATION_CODE_CACHE);
        if (cache != null) {
            cache.put(phone, code);
        }
        
        // 在实际应用中，这里应该调用短信服务发送验证码
        log.info("向手机号 {} 发送验证码: {}", phone, code);
        
        return code;
    }

    /**
     * 验证验证码
     * 从缓存中获取验证码进行比对，验证成功后自动删除缓存
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 是否有效，true表示验证通过
     */
    @Override
    public boolean verifyCode(String phone, String code) {
        Cache cache = cacheManager.getCache(VERIFICATION_CODE_CACHE);
        if (cache != null) {
            String cachedCode = cache.get(phone, String.class);
            boolean isValid = Objects.equals(cachedCode, code);
            
            if (isValid) {
                // 验证成功后删除缓存
                cache.evict(phone);
            }
            
            return isValid;
        }
        return false;
    }
    
    /**
     * 生成6位随机数字验证码
     *
     * @return 6位数字验证码
     */
    private String generateRandomCode() {
        // 生成6位数字验证码
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
} 