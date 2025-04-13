package com.keji.green.lit.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 面试信息
 * @author xiangjun_lee
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewInfo {

    /**
     * 面试id
     */
    private String interviewId;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 面试名称
     */
    private String interviewName;

    /**
     * 面试语言
     */
    private String interviewLanguage;

    /**
     * 编程语言
     */
    private String programmingLanguage;

    /**
     * 职位信息
     */
    private String positionInfo;

    /**
     * 额外数据
     */
    private String extraData;

    /**
     * 面试开始时间
     */
    private Date startTime;

    /**
     * 面试结束时间
     */
    private Date endTime;

    /**
     * 面试状态
     */
    private Integer status;

    /**
     * 职位要求
     */
    private String jobRequirements;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 语音转文字总时长(秒)
     */
    private Long sttTotalSeconds;

    /**
     * 语音转文字总费用(分)
     */
    private Long sttTotalCost;

    /**
     * 智能问答总次数
     */
    private Integer agTotalCount;

    /**
     * 智能问答总费用(分)
     */
    private Long agTotalCost;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;
}