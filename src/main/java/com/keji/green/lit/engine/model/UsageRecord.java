package com.keji.green.lit.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author xiangjun_lee
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageRecord {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 面试ID
     */
    private String interviewId;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 使用类型
     */
    private Integer usageType;

    /**
     * 使用时长（秒）
     */
    private Long durationSeconds;

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