package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.service.RateLimitService;
import com.keji.green.lit.engine.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

/**
 * 频率限制服务实现类
 * 使用Redis实现访问频率限制
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitService {

    /**
     * Redis工具类
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
        // 使用 Redis 的 INCR 命令原子性地增加计数并获取新值
        int count = Integer.parseInt(redisUtils.incrAndGet(redisKey));
        // 如果是第一次访问，设置过期时间
        if (count == INTEGER_ONE) {
            redisUtils.expire(redisKey, periodSeconds);
        }
        return count > limit;
    }
} 