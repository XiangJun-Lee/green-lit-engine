package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.request.ResumeEnhanceRequest;
import com.keji.green.lit.engine.dto.response.ResumeEnhanceResponse;

/**
 * 简历美化服务接口
 */
public interface ResumeService {
    /**
     * 美化简历
     * @param request 美化请求
     * @return 美化后的简历
     */
    ResumeEnhanceResponse enhanceResume(ResumeEnhanceRequest request);
} 