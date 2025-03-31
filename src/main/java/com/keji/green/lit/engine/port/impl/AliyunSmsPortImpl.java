package com.keji.green.lit.engine.port.impl;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.port.SmsPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * 阿里云短信服务实现
 * 实现短信服务的防腐层接口
 */
@Slf4j
@Component
public class AliyunSmsPortImpl implements SmsPort {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.template-code}")
    private String templateCode;

    @Override
    public boolean sendVerificationCode(String phone, String code) {
        try {
            // TODO: 实现阿里云短信发送逻辑
            // 这里需要集成阿里云SDK，调用发送短信的API
            // 示例代码：
            // IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            // IAcsClient client = new DefaultAcsClient(profile);
            // CommonRequest request = new CommonRequest();
            // request.setSysMethod(MethodType.POST);
            // request.setSysDomain("dysmsapi.aliyuncs.com");
            // request.setSysVersion("2017-05-25");
            // request.setSysAction("SendSms");
            // request.putQueryParameter("PhoneNumbers", phone);
            // request.putQueryParameter("SignName", signName);
            // request.putQueryParameter("TemplateCode", templateCode);
            // request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
            // CommonResponse response = client.getCommonResponse(request);
            // return response.getHttpResponse().isSuccess();
            
            // 临时返回true，实际实现时需要替换为真实的发送结果
            return true;
        } catch (Exception e) {
            log.error("发送短信失败，手机号：{}，验证码：{}", phone, code, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR, "发送短信失败", e);
        }
    }

    @Override
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
} 