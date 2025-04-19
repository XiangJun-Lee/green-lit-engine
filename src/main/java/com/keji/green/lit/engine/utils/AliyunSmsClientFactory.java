package com.keji.green.lit.engine.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;

/**
 * 阿里云短信客户端工厂类
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
public class AliyunSmsClientFactory {

    /**
     * 创建阿里云短信客户端
     *
     * @param accessKeyId 访问密钥ID
     * @param accessKeySecret 访问密钥密码
     * @param endpoint 服务端点
     * @return 阿里云短信客户端
     * @throws Exception 创建客户端异常
     */
    public static Client createClient(String accessKeyId, String accessKeySecret, String endpoint) throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = endpoint;
        return new Client(config);
    }
} 