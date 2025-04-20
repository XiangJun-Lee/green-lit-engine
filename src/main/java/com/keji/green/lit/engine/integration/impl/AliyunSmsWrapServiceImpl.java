package com.keji.green.lit.engine.integration.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.keji.green.lit.engine.enums.VerificationCodeScene;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.integration.SmsWrapService;
import com.keji.green.lit.engine.utils.AliyunSmsClientFactory;
import com.keji.green.lit.engine.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 阿里云短信服务实现类
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
@Slf4j
@Service("aliyunSmsWrapService")
public class AliyunSmsWrapServiceImpl implements SmsWrapService {

    private final Random random = new Random();

    @Override
    public boolean sendVerificationCode(String phone, VerificationCodeScene codeScene, String code) {
        try {
            // 创建阿里云短信客户端
            Client client = AliyunSmsClientFactory.createClient();

            // 构建短信模板参数
            Map<String, String> templateParam = new HashMap<>();
            templateParam.put("code", code);

            // 构建发送短信请求
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName("阿里云短信测试")
                    .setTemplateCode("SMS_154950909")
                    .setTemplateParam(JsonUtils.toJson(templateParam));

            // 发送短信
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            
            // 记录发送日志
            log.info("发送短信验证码，手机号：{}，验证码：{}，响应：{}", phone, code, JSON.toJSONString(response));

            // 判断发送是否成功
            return "OK".equals(response.getBody().getCode());
        } catch (Exception e) {
            log.error("发送短信失败，手机号：{}，验证码：{}", phone, code, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "网络异常，请稍后重试", e);
        }
    }

    @Override
    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 