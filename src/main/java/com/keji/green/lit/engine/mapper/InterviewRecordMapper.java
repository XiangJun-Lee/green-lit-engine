package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.InterviewRecordWithBLOBs;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author xiangjun_lee
 */
@Mapper
public interface InterviewRecordMapper {

    Long insertSelective(InterviewRecordWithBLOBs row);

    InterviewRecordWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InterviewRecordWithBLOBs row);

    List<InterviewRecordWithBLOBs> selectQuestionByInterviewId(Map<String, Object> params);

}