package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.response.QuestionAnswerRecordListQueryParam;
import com.keji.green.lit.engine.model.QuestionAnswerRecord;

import java.util.List;
import java.util.Map;

/**
 * @author xiangjun_lee
 * @date 2025/4/14 23:37
 */
public interface QuestionAnswerRecordService {

    /**
     * 滚动查询面试记录
     */
    List<QuestionAnswerRecord> questionAnswerRecordsList(QuestionAnswerRecordListQueryParam param);


    /**
     * 创建面试记录
     * @param record
     * @return
     */
    boolean createQuestionAnswerRecord(QuestionAnswerRecord record);

}
