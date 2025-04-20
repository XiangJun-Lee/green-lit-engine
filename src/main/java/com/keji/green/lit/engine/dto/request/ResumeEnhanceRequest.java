package com.keji.green.lit.engine.dto.request;

import lombok.Data;

/**
 * 简历美化请求DTO
 * @author xiangjun_lee
 */
@Data
public class ResumeEnhanceRequest {
    /**
     * 原始简历文本
     */
    private String resumeText;
} 