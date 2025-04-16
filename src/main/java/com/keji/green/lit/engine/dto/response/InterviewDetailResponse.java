package com.keji.green.lit.engine.dto.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 面试详情响应
 *
 * @author xiangjun_lee
 */
@Data
public class InterviewDetailResponse {

    /**
     * 面试ID
     */
    private String interviewId;

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
     * 职位要求
     */
    private String jobRequirements;

    /**
     * 是否开启联网模式
     */
    private Boolean onlineMode;

    /**
     * 是否开启语音触发
     */
    private Boolean voiceTrigger;

    /**
     * 语音触发词列表
     */
    private String voiceTriggerWords;

    /**
     * 快捷键配置
     * key: 快捷键功能
     * value: 对应快捷键
     */
    private String shortcutConfig;

    /**
     * 简历文本内容
     */
    private String resumeText;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 总问题数
     */
    private Long agTotalCost;

    /**
     * 余额
     */
    private Long balance;

    /**
     * 面试提问记录
     */
    private List<QuestionAnswerRecordResponse> records;
} 