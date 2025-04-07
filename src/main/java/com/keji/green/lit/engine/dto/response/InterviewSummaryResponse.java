package com.keji.green.lit.engine.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * 面试总结响应
 * @author xiangjun_lee
 */
@Data
public class InterviewSummaryResponse {
    
    /**
     * 面试ID
     */
    private String interviewId;
    
    /**
     * 开始时间
     */
    private Date startTime;
    
    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 状态
     */
    private Integer status;
} 