package com.keji.green.lit.engine.service;

/**
 * 频率限制服务接口
 * 提供接口访问频率限制的功能
 */
public interface RateLimitService {
    
    /**
     * 检查是否超过频率限制，并记录访问次数
     * 
     * @param key 限制的键（如IP地址、手机号等）
     * @param limit 限制次数
     * @param periodSeconds 时间周期（秒）
     * @return 是否超过限制，true表示超过限制
     */
    boolean isRateLimited(String key, int limit, int periodSeconds);
} 