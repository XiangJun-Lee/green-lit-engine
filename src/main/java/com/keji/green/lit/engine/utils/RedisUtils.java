package com.keji.green.lit.engine.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * Redis工具类
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
@Slf4j
@Component
public class RedisUtils {

    @Resource
    private JedisPool jedisPool;

    /**
     * 获取 Jedis 实例
     */
    private Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 释放 Jedis 实例
     */
    private void closeJedis(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 设置字符串值
     */
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.set(key, value);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 设置字符串值并设置过期时间
     */
    public String set(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.setex(key, seconds, value);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 获取字符串值
     */
    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.get(key);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 删除键
     */
    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(key);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 设置哈希值
     */
    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hset(key, field, value);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 获取哈希值
     */
    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hget(key, field);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 删除哈希字段
     */
    public Long hdel(String key, String... fields) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hdel(key, fields);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 获取所有哈希字段
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.hgetAll(key);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 设置过期时间
     */
    public Long expire(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.expire(key, seconds);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 检查键是否存在
     */
    public Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.exists(key);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 获取剩余过期时间
     */
    public Long ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.ttl(key);
        } finally {
            closeJedis(jedis);
        }
    }

    public long incr(String redisKey) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.incr(redisKey);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 增加计数并获取新值
     */
    public String incrAndGet(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return String.valueOf(jedis.incr(key));
        } finally {
            closeJedis(jedis);
        }
    }
}