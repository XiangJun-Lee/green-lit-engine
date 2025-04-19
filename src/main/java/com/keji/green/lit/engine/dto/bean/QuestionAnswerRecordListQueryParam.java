package com.keji.green.lit.engine.dto.bean;

import lombok.Data;

/**
 * @author xiangjun_lee
 * @date 2025/4/14 23:49
 */
@Data
public class QuestionAnswerRecordListQueryParam {

    /**
     * 面试id
     */
    private String interviewId;

    /**
     * 每页数量
     */
    private Integer limit;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 是否降序
     */
    private Boolean orderByDesc;
}
