package com.keji.green.lit.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.timeout:2000}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait:3000}")
    private long maxWait;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 设置最大连接数
        poolConfig.setMaxTotal(maxActive);
        // 设置最大空闲连接数
        poolConfig.setMaxIdle(maxIdle);
        // 设置最小空闲连接数
        poolConfig.setMinIdle(minIdle);
        // 设置获取连接时的最大等待时间
        poolConfig.setMaxWaitMillis(maxWait);
        // 设置连接池是否阻塞
        poolConfig.setBlockWhenExhausted(true);
        // 设置连接池的测试配置
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);
        // 设置连接池的驱逐配置
        poolConfig.setTimeBetweenEvictionRunsMillis(30000);
        poolConfig.setMinEvictableIdleTimeMillis(60000);
        poolConfig.setNumTestsPerEvictionRun(3);

        poolConfig.setJmxEnabled(false);

        // 使用空字符串作为密码
        return new JedisPool(poolConfig, host, port, timeout);
    }
} 