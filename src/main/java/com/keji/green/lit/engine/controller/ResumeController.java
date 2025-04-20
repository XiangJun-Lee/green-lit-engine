package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.request.ResumeEnhanceRequest;
import com.keji.green.lit.engine.dto.response.ResumeEnhanceResponse;
import com.keji.green.lit.engine.service.ResumeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简历美化控制器
 * @author xiangjun_lee
 */
@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Resource
    private ResumeService resumeService;

    /**
     * 美化简历
     * @param request 美化请求
     * @return 美化后的简历
     */
    @PostMapping("/enhance")
    public ResumeEnhanceResponse enhanceResume(@RequestBody ResumeEnhanceRequest request) {
        return resumeService.enhanceResume(request);
    }
} 