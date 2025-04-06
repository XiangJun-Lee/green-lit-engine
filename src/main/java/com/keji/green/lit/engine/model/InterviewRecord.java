package com.keji.green.lit.engine.model;

import lombok.Data;

import java.util.Date;

/**
 * 面试记录基础信息
 * @author xiangjun_lee
 */
@Data
public class InterviewRecord {

    /**
     * id
     */
    private Long id;

    /**
     * 面试记录id
     */
    private String interviewId;

    /**
     * 面试记录创建时间
     */
    private Date gmtCreate;


    /**
     * 面试记录修改时间
     */
    private Date gmtModify;
}