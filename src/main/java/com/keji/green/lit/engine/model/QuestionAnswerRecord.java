package com.keji.green.lit.engine.model;

import lombok.Data;

import java.util.Date;

/**
 * @author xiangjun_lee
 */
@Data
public class QuestionAnswerRecord {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 关联的面试ID
     */
    private String interviewId;

    /**
     * 面试问题
     */
    private String question;

    /**
     * 面试答案
     */
    private String answer;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;
}