package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.model.InterviewInfo;

/**
 * @author xiangjun_lee
 * @date 2025/4/20 10:43
 */
public interface TransactionalService {

    /**
     * 快速回答收费
     */
    void fastAnswerCharging(InterviewInfo interviewInfo);
}
