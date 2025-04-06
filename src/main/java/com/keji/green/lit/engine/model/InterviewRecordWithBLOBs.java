package com.keji.green.lit.engine.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 面试记录详细详细
 * @author xiangjun_lee
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterviewRecordWithBLOBs extends InterviewRecord {

    /**
     * 问题
     */
    private String question;

    /**
     * 答案
     */
    private String answer;
}