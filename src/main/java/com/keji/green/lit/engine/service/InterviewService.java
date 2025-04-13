package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.request.AskQuestionRequest;
import com.keji.green.lit.engine.dto.request.CreateInterviewRequest;
import com.keji.green.lit.engine.dto.request.RecordSttUsageRequest;
import com.keji.green.lit.engine.dto.request.UpdateInterviewRequest;
import com.keji.green.lit.engine.dto.response.InterviewDetailResponse;
import com.keji.green.lit.engine.dto.response.InterviewCreateResponse;
import com.keji.green.lit.engine.dto.response.InterviewInfoResponse;
import com.keji.green.lit.engine.dto.response.PageResponse;
import com.keji.green.lit.engine.dto.response.UpdateInterviewResponse;
import com.keji.green.lit.engine.enums.InterviewStatus;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 面试服务接口
 * 提供面试创建、问答交互、结束及查询功能
 * 
 * @author xiangjun_lee
 */
public interface InterviewService {
    
    /**
     * 创建面试会话
     * 生成UUID作为面试ID，记录创建时间
     * 
     * @param request 创建面试请求
     * @return 面试会话信息
     */
    InterviewCreateResponse createInterview(CreateInterviewRequest request);
    
    /**
     * 提问并获取答案（流式）
     * 1. 检查用户积分是否充足
     * 2. 扣除积分
     * 3. 记录面试流水（问题）
     * 4. 调用算法服务获取答案
     * 5. 流式返回答案
     * 6. 更新面试流水（答案）
     * 
     * @param interviewId 面试ID
     * @param request 问题请求
     * @return SSE流式响应
     */
    SseEmitter askQuestion(String interviewId, AskQuestionRequest request);
    
    /**
     * 结束面试
     * 记录结束时间，计算面试时长，计算并扣除积分
     * 
     * @param interviewId 面试ID
     * @return 面试总结信息
     */
    InterviewInfoResponse endInterview(String interviewId);
    
    /**
     * 获取面试详情
     * 包括面试基本信息和问答流水记录
     * 
     * @param interviewId 面试ID
     * @return 面试详情
     */
    InterviewDetailResponse getInterviewDetail(String interviewId);
    
    /**
     * 分页获取面试列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 面试状态（可选）
     * @return 分页面试列表
     */
    PageResponse<InterviewInfoResponse> getInterviewList(Integer pageNum, Integer pageSize, InterviewStatus status);

    /**
     * 更新面试信息
     *
     * @param interviewId
     * @param request     更新请求
     * @return 更新结果
     */
    UpdateInterviewResponse updateInterview(String interviewId, UpdateInterviewRequest request);

    /**
     * 记录 STT 使用情况
     * 使用分布式锁控制并发，保证数据一致性
     *
     * @param interviewId 面试ID
     * @param request 使用情况请求
     */
    void recordSttUsage(String interviewId, RecordSttUsageRequest request);
} 