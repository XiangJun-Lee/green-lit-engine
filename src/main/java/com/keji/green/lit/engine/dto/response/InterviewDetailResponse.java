package com.keji.green.lit.engine.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 面试详情响应
 * @author xiangjun_lee
 */
@Data
public class InterviewDetailResponse {
    
    /**
     * 面试ID
     */
    private String interviewId;
    
    /**
     * 面试状态
     */
    private Integer status;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;
    
    /**
     * 面试时长（分钟）
     */
    private Long durationMinutes;
    
    /**
     * 消耗的总积分
     */
    private Integer totalPoints;
    
    /**
     * 面试提问记录
     */
    private List<InterviewRecordResponse> records;
} 