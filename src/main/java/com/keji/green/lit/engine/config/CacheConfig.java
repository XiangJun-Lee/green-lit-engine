package com.keji.green.lit.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 缓存配置类
 * 配置缓存管理器和缓存空间，用于存储验证码等临时数据
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 验证码过期时间（秒）
     */
    @Value("${sms.verification.expiration:90}")
    private int verificationCodeExpiration;

    /**
     * 缓存管理器配置
     * 定义系统使用的缓存区域
     * 
     * @return 简单缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("verificationCodes")
        ));
        return cacheManager;
    }
} 