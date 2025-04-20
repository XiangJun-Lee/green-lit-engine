package com.keji.green.lit.engine.dto.response;

import lombok.Data;

/**
 * 简历美化响应
 * @author xiangjun_lee
 */
@Data
public class ResumeEnhanceResponse {
    /**
     * 美化后的简历文本
     */
    private String enhancedResume;
} 