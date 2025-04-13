package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.request.UpdateInterviewRequest;
import com.keji.green.lit.engine.dto.response.*;
import com.keji.green.lit.engine.dto.request.AskQuestionRequest;
import com.keji.green.lit.engine.dto.request.CreateInterviewRequest;
import com.keji.green.lit.engine.dto.request.RecordSttUsageRequest;
import com.keji.green.lit.engine.enums.InterviewStatus;
import com.keji.green.lit.engine.service.InterviewService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 面试控制器
 * 处理面试创建、问答流程、结束及查询等功能
 * @author xiangjun_lee
 */
@Validated
@RestController
@RequestMapping("/api/interview")
public class InterviewController {

    @Resource
    private InterviewService interviewService;

    /**
     * 创建面试会话
     * 生成面试ID，记录创建时间
     * @param request 创建面试请求，包含面试名称和额外配置
     * @return 面试会话响应，包含面试ID等信息
     */
    @PostMapping("/create")
    public Result<InterviewCreateResponse> createInterview(@Valid @RequestBody CreateInterviewRequest request) {
        return Result.success(interviewService.createInterview(request));
    }

    /**
     * 更新面试信息
     */
    @PostMapping("/{interviewId}/update")
    public Result<UpdateInterviewResponse> updateInterview(@PathVariable String interviewId,
                                                           @Valid @RequestBody UpdateInterviewRequest request) {
        return Result.success(interviewService.updateInterview(interviewId, request));
    }

    /**
     * 提问接口（流式）
     * 1. 检查用户积分
     * 2. 扣除积分
     * 3. 记录面试流水（问题）
     * 4. 调用算法服务获取答案
     * 5. 流式返回答案
     * 6. 更新面试流水（答案）
     * @param interviewId 面试ID
     * @param request 问题请求
     * @return 流式应答
     */
    @PostMapping("/{interviewId}/ask")
    public SseEmitter askQuestion(
            @PathVariable String interviewId,
            @Valid @RequestBody AskQuestionRequest request) {
        return interviewService.askQuestion(interviewId, request);
    }

    /**
     * 结束面试
     * 记录结束时间，计算面试时长，计算并扣除积分
     * @param interviewId 面试ID
     * @return 面试总结信息
     */
    @PostMapping("/{interviewId}/end")
    public Result<InterviewInfoResponse> endInterview(@PathVariable String interviewId) {
        return Result.success(interviewService.endInterview(interviewId));
    }

    /**
     * 获取面试详情
     * 包括面试基本信息和问答流水记录
     * @param interviewId 面试ID
     * @return 面试详情
     */
    @GetMapping("/{interviewId}")
    public Result<InterviewDetailResponse> getInterviewDetail(@PathVariable String interviewId) {
        return Result.success(interviewService.getInterviewDetail(interviewId));
    }

    /**
     * 获取面试列表
     * 分页查询用户的面试记录
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 面试状态（可选）
     * @return 分页面试列表
     */
    @GetMapping("/list")
    public Result<PageResponse<InterviewInfoResponse>> getInterviewList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) InterviewStatus status) {
        return Result.success(interviewService.getInterviewList(pageNum, pageSize, status));
    }

    /**
     * 记录 STT 使用情况
     *
     * @param interviewId 面试ID
     * @param request 使用情况请求
     */
    @PostMapping("/{interviewId}/stt/record-usage")
    public Result<Void> recordSttUsage(
            @PathVariable String interviewId,
            @Valid @RequestBody RecordSttUsageRequest request) {
        interviewService.recordSttUsage(interviewId, request);
        return Result.success();
    }
} 