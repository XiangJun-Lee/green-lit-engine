package com.keji.green.lit.engine.dto.response;

import com.keji.green.lit.engine.enums.InterviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面试列表项响应
 * @author xiangjun_lee
 */
@Data
public class InterviewListResponse {
    
    /**
     * 面试ID
     */
    private String interviewId;
    
    /**
     * 面试状态
     */
    private InterviewStatus status;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间（如果已结束）
     */
    private LocalDateTime endTime;
    
    /**
     * 消耗的总积分
     */
    private Integer totalPoints;
    
    /**
     * 问题数量
     */
    private Integer questionCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 