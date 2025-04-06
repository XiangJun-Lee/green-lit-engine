package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.InterviewRecordWithBLOBs;

/**
 * @author xiangjun_lee
 */
public interface InterviewRecordMapper {

    int insertSelective(InterviewRecordWithBLOBs row);

    InterviewRecordWithBLOBs selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InterviewRecordWithBLOBs row);

}