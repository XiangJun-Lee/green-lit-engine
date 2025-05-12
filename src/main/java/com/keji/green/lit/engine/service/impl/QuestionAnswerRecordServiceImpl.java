package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dto.response.QuestionAnswerRecordListQueryParam;
import com.keji.green.lit.engine.mapper.QuestionAnswerRecordMapper;
import com.keji.green.lit.engine.model.QuestionAnswerRecord;
import com.keji.green.lit.engine.service.QuestionAnswerRecordService;
import com.keji.green.lit.engine.utils.EncryptionUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiangjun_lee
 * @date 2025/4/14
 */
@Service
public class QuestionAnswerRecordServiceImpl implements QuestionAnswerRecordService {

    @Resource
    private QuestionAnswerRecordMapper questionAnswerRecordMapper;

    @Override
    public List<QuestionAnswerRecord> questionAnswerRecordsList(QuestionAnswerRecordListQueryParam param) {
        // 获取面试提问信息
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("interviewId", param.getInterviewId());
        if (Objects.isNull(param.getLimit()) || param.getLimit() > 50) {
            param.setLimit(50);
        }
        queryParam.put("limit", param.getLimit());
        if (StringUtils.isNotBlank(param.getOrderBy())) {
            if (BooleanUtils.isTrue(param.getOrderByDesc())) {
                queryParam.put("orderByDesc", param.getOrderBy());
            } else {
                queryParam.put("orderByAsc", param.getOrderBy());
            }
        }
        List<QuestionAnswerRecord> questionAnswerRecordList = questionAnswerRecordMapper.selectListByInterviewId(queryParam);
        if (CollectionUtils.isNotEmpty(questionAnswerRecordList)) {
            for (QuestionAnswerRecord record : questionAnswerRecordList) {
                if (StringUtils.isNotBlank(record.getQuestion())) {
                    record.setQuestion(EncryptionUtils.decrypt(record.getQuestion()));
                }
                if (StringUtils.isNotBlank(record.getAnswer())) {
                    record.setAnswer(EncryptionUtils.decrypt(record.getAnswer()));
                }
            }
        }
        return questionAnswerRecordList;
    }

    @Override
    public boolean createQuestionAnswerRecord(QuestionAnswerRecord record) {
        if (StringUtils.isNotBlank(record.getQuestion())) {
            record.setQuestion(EncryptionUtils.encrypt(record.getQuestion()));
        }
        if (StringUtils.isNotBlank(record.getAnswer())) {
            record.setAnswer(EncryptionUtils.encrypt(record.getAnswer()));
        }
        return questionAnswerRecordMapper.insertSelective(record) > 0;
    }
} 