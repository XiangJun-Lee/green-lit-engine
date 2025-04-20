package com.keji.green.lit.engine.utils;

import com.aliyun.credentials.Client;
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
     * @return 阿里云短信客户端
     * @throws Exception 创建客户端异常
     */
    public static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        Client credential = new Client();
        Config config = new Config().setCredential(credential);
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }
} 