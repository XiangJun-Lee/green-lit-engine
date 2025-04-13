package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.QuestionAnswerRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionAnswerRecordMapper {

    int insertSelective(QuestionAnswerRecord record);

    int deleteByPrimaryKey(Long id);

    QuestionAnswerRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(QuestionAnswerRecord row);

    int updateByPrimaryKey(QuestionAnswerRecord row);

    List<QuestionAnswerRecord> selectByInterviewId(String interviewId);

    int countByInterviewId(String interviewId);

    List<QuestionAnswerRecord> selectListByInterviewId(Map<String, Object> params);

}