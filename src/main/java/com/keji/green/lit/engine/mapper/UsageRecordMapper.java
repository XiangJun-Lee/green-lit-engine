package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.UsageRecord;

import java.util.List;

public interface UsageRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insertSelective(UsageRecord row);

    UsageRecord selectByPrimaryKey(Long id);

    List<UsageRecord> selectByInterviewId(String interviewId);

    List<UsageRecord> selectByUid(Long uid);
}