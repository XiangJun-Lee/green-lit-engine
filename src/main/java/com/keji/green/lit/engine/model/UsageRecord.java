package com.keji.green.lit.engine.model;

import lombok.Data;
import java.util.Date;

@Data
public class UsageRecord {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 面试ID
     */
    private Long interviewId;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 使用类型
     */
    private String usageType;

    /**
     * 使用时长（秒）
     */
    private Integer durationSeconds;

    /**
     * 费用（分）
     */
    private Long costInCents;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;
}