package com.keji.green.lit.engine.dto.response;

import lombok.Data;

import java.util.Date;

/**
 * 面试总结响应
 * @author xiangjun_lee
 */
@Data
public class InterviewInfoResponse {

    /**
     * 面试ID
     */
    private String interviewId;

    /**
     * 面试名称
     */
    private String interviewName;

    /**
     * 面试状态
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间（如果已结束）
     */
    private Date endTime;

    /**
     * 总时长（分钟）
     */
    private Long totalMinutes;

    /**
     * 总问题数
     */
    private Long agTotalCost;

    /**
     * 创建时间
     */
    private Date createTime;
} 