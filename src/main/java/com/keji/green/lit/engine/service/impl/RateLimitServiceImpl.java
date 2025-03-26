package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.service.RateLimitService;
import com.keji.green.lit.engine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 频率限制服务实现类
 * 使用Redis实现访问频率限制
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitService {

    /**
     * Redis模板
     */
    @Resource
    private RedisUtils redisUtils;
    
    /**
     * 频率限制键前缀
     */
    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    @Override
    public boolean isRateLimited(String key, int limit, int periodSeconds) {
        String redisKey = RATE_LIMIT_KEY_PREFIX + key;
        String countString = redisUtils.get(redisKey);
        if (StringUtils.isBlank(countString)){
            return false;
        }
        int count = Integer.parseInt(countString);
        return count >= limit;
    }

    @Override
    public void recordAccess(String key) {
        String redisKey = RATE_LIMIT_KEY_PREFIX + key;
        redisUtils.incr(redisKey);
        // 设置过期时间
        redisUtils.expire(redisKey, 3600);
    }
} 