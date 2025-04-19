package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.bean.QuestionAnswerRecordListQueryParam;
import com.keji.green.lit.engine.model.QuestionAnswerRecord;

import java.util.List;

/**
 * @author xiangjun_lee
 * @date 2025/4/14 23:37
 */
public interface QuestionAnswerRecordService {

    /**
     * 滚动查询面试记录
     */
    List<QuestionAnswerRecord> questionAnswerRecordsList(QuestionAnswerRecordListQueryParam param);
}
